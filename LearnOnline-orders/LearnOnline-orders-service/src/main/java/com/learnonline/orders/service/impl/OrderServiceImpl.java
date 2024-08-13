package com.learnonline.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.base.utils.IdWorkerUtils;
import com.learnonline.base.utils.QRCodeUtil;
import com.learnonline.messagesdk.model.po.MqMessage;
import com.learnonline.orders.config.AlipayConfig;
import com.learnonline.orders.mapper.XcPayRecordMapper;
import com.learnonline.orders.model.dto.AddOrderDto;
import com.learnonline.orders.model.dto.PayRecordDto;
import com.learnonline.orders.model.dto.PayStatusDto;
import com.learnonline.orders.model.po.XcOrders;
import com.learnonline.orders.model.po.XcOrdersGoods;
import com.learnonline.orders.model.po.XcPayRecord;
import com.learnonline.orders.service.OrderService;
import com.learnonline.orders.mapper.XcOrdersGoodsMapper;
import com.learnonline.orders.mapper.XcOrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.orders.service.impl
 * @Author: ASUS
 * @CreateTime: 2024-08-13  10:49
 * @Description: 订单业务接口实现类
 * @Version: 1.0
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    XcOrdersMapper ordersMapper;
    @Autowired
    XcOrdersGoodsMapper ordersGoodsMapper;

    @Autowired
    XcPayRecordMapper payRecordMapper;
    @Autowired
    XcPayRecordMapper xcPayRecordMapper;
    @Autowired
    XcOrdersGoodsMapper xcOrdersGoodsMapper;

    @Autowired
    XcOrdersMapper xcOrdersMapper;
    @Value("${pay.qrcodeurl}")
    String qrcodeurl;

    @Autowired
    OrderServiceImpl currentProxy;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Transactional
    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {

        //插入订单表,订单主表，订单明细表
        XcOrders xcOrders = saveXcOrders(userId, addOrderDto);

        //插入支付记录
        XcPayRecord payRecord = createPayRecord(xcOrders);
        Long payNo = payRecord.getPayNo();

        //生成二维码
        QRCodeUtil qrCodeUtil = new QRCodeUtil();
        //支付二维码的url
        String url = String.format(qrcodeurl, payNo);
        //二维码图片
        String qrCode = null;
        try {
            qrCode = qrCodeUtil.createQRCode(url, 200, 200);
        } catch (IOException e) {
            LearnOnlineException.cast("生成二维码出错");
        }

        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        payRecordDto.setQrcode(qrCode);

        return payRecordDto;
    }

    @Override
    public XcPayRecord getPayRecordByPayNo(String payNo) {
        XcPayRecord xcPayRecord = payRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>().eq(XcPayRecord::getPayNo, payNo));
        return xcPayRecord;
    }

    @Override
    public PayRecordDto queryPayResult(String payNo){
        XcPayRecord payRecord = getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            LearnOnlineException.cast("请重新点击支付获取二维码");
        }
        //支付状态
        String status = payRecord.getStatus();
        //如果支付成功直接返回
        if ("601002".equals(status)) {
            PayRecordDto payRecordDto = new PayRecordDto();
            BeanUtils.copyProperties(payRecord, payRecordDto);
            return payRecordDto;
        }
        //从支付宝查询支付结果
        PayStatusDto payStatusDto = queryPayResultFromAlipay(payNo);
        //保存支付结果
        currentProxy.saveAliPayStatus( payStatusDto);
        //重新查询支付记录
        payRecord = getPayRecordByPayNo(payNo);
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        return payRecordDto;

    }


    /**
     * @description 保存支付宝支付结果
     * @param payStatusDto  支付结果信息 从支付宝查询到的信息
     * @return void
     */
    @Transactional
    @Override
    public void saveAliPayStatus(PayStatusDto payStatusDto) {
        // 1. 获取支付流水号
        String payNo = payStatusDto.getOut_trade_no();
        // 2. 查询数据库订单状态
        XcPayRecord payRecord = getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            LearnOnlineException.cast("未找到支付记录");
        }
        XcOrders order = xcOrdersMapper.selectById(payRecord.getOrderId());
        if (order == null) {
            LearnOnlineException.cast("找不到相关联的订单");
        }
        String statusFromDB = payRecord.getStatus();
        // 2.1 已支付，直接返回
        if ("600002".equals(statusFromDB)) {
            return;
        }
        // 3. 查询支付宝交易状态
        String tradeStatus = payStatusDto.getTrade_status();
        // 3.1 支付宝交易已成功，保存订单表和交易记录表，更新交易状态
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            // 更新支付交易表
            payRecord.setStatus("601002");
            payRecord.setOutPayNo(payStatusDto.getTrade_no());
            payRecord.setOutPayChannel("Alipay");
            payRecord.setPaySuccessTime(LocalDateTime.now());
            int updateRecord = xcPayRecordMapper.updateById(payRecord);
            if (updateRecord <= 0) {
                LearnOnlineException.cast("更新支付交易表失败");
            }
            // 更新订单表
            order.setStatus("600002");
            int updateOrder = xcOrdersMapper.updateById(order);
            if (updateOrder <= 0) {
                log.debug("更新订单表失败");
                LearnOnlineException.cast("更新订单表失败");
            }
        }
    }

    /**
     * 请求支付宝查询支付结果
     * @param payNo 支付交易号
     * @return 支付结果
     */
    /**
     * 调用支付宝接口查询支付结果
     *
     * @param payNo 支付记录id
     * @return 支付记录信息
     */
    public PayStatusDto queryPayResultFromAlipay(String payNo) {
        // 1. 获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        AlipayTradeQueryResponse response = null;
        // 2. 请求查询
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            LearnOnlineException.cast("请求支付宝查询支付结果异常");
        }
        // 3. 查询失败
        if (!response.isSuccess()) {
            LearnOnlineException.cast("请求支付宝查询支付结果异常");
        }
        // 4. 查询成功，获取结果集
        String resultJson = response.getBody();
        // 4.1 转map
        Map resultMap = JSON.parseObject(resultJson, Map.class);
        // 4.2 获取我们需要的信息
        Map<String, String> alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
        // 5. 创建返回对象
        PayStatusDto payStatusDto = new PayStatusDto();
        // 6. 封装返回
        String tradeStatus = alipay_trade_query_response.get("trade_status");
        String outTradeNo = alipay_trade_query_response.get("out_trade_no");
        String tradeNo = alipay_trade_query_response.get("trade_no");
        String totalAmount = alipay_trade_query_response.get("total_amount");
        payStatusDto.setTrade_status(tradeStatus);
        payStatusDto.setOut_trade_no(outTradeNo);
        payStatusDto.setTrade_no(tradeNo);
        payStatusDto.setTotal_amount(totalAmount);
        payStatusDto.setApp_id(APP_ID);
        return payStatusDto;
    }


    /**
     * 保存订单信息
     * @param userId
     * @param addOrderDto
     * @return
     */
    public XcOrders saveXcOrders(String userId, AddOrderDto addOrderDto){
        // 1. 幂等性判断
        XcOrders order = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if (order != null) {
            return order;
        }
        // 2. 插入订单表
        order = new XcOrders();
        BeanUtils.copyProperties(addOrderDto, order);
        order.setId(IdWorkerUtils.getInstance().nextId());
        order.setCreateDate(LocalDateTime.now());
        order.setUserId(userId);
        order.setStatus("600001");
        int insert = xcOrdersMapper.insert(order);
        if (insert <= 0) {
            LearnOnlineException.cast("插入订单记录失败");
        }
        // 3. 插入订单明细表
        Long orderId = order.getId();
        String orderDetail = addOrderDto.getOrderDetail();
        List<XcOrdersGoods> xcOrdersGoodsList = JSON.parseArray(orderDetail, XcOrdersGoods.class);
        xcOrdersGoodsList.forEach(goods -> {
            goods.setOrderId(orderId);
            int insert1 = xcOrdersGoodsMapper.insert(goods);
            if (insert1 <= 0) {
                LearnOnlineException.cast("插入订单明细失败");
            }
        });
        return order;

    }

    /**
     * 根据业务id查询订单 ,业务id是选课记录表中的主键
     * @param businessId
     * @return
     */
    public XcOrders getOrderByBusinessId(String businessId) {
        XcOrders orders = ordersMapper.selectOne(new LambdaQueryWrapper<XcOrders>().eq(XcOrders::getOutBusinessId, businessId));
        return orders;
    }


    /**
     * 保存支付记录
     * @param orders
     * @return
     */
    public XcPayRecord createPayRecord(XcOrders orders) {
        if (orders == null) {
            LearnOnlineException.cast("订单不存在");
        }
        if ("600002".equals(orders.getStatus())) {
            LearnOnlineException.cast("订单已支付");
        }
        XcPayRecord payRecord = new XcPayRecord();
        payRecord.setPayNo(IdWorkerUtils.getInstance().nextId());
        payRecord.setOrderId(orders.getId());
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus("601001");  // 未支付
        payRecord.setUserId(orders.getUserId());
        int insert = xcPayRecordMapper.insert(payRecord);
        if (insert <= 0) {
            LearnOnlineException.cast("插入支付交易记录失败");
        }
        return payRecord;
    }




}

