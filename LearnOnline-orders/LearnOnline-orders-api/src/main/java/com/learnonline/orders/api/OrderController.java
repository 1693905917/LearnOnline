package com.learnonline.orders.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.learnonline.base.execption.LearnOnlineException;
import com.learnonline.orders.model.dto.AddOrderDto;
import com.learnonline.orders.model.dto.PayRecordDto;
import com.learnonline.orders.model.dto.PayStatusDto;
import com.learnonline.orders.model.po.XcPayRecord;
import com.learnonline.orders.util.SecurityUtil;
import com.learnonline.orders.config.AlipayConfig;
import com.learnonline.orders.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.orders.api
 * @Author: ASUS
 * @CreateTime: 2024-08-13  10:46
 * @Description: 订单支付接口
 * @Version: 1.0
 */
@Api(value = "订单支付接口", tags = "订单支付接口")
@Slf4j
@Controller
public class OrderController {
    @Autowired
    OrderService orderService;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;
    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @ApiOperation("生成支付二维码")
    @PostMapping("/generatepaycode")
    @ResponseBody
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto) {

        SecurityUtil.XcUser user = SecurityUtil.getUser();
        String userId = user.getId();

        //调用service，完成插入订单信息、插入支付记录、生成支付二维码
        PayRecordDto payRecordDto = orderService.createOrder(userId, addOrderDto);

        return payRecordDto;
    }


    @ApiOperation("扫码下单接口")
    @GetMapping("/requestpay")
    public void requestpay(String payNo,HttpServletResponse httpResponse) throws IOException {
        //如果payNo不存在则提示重新发起支付
        XcPayRecord payRecord = orderService.getPayRecordByPayNo(payNo);
        if(payRecord == null){
            LearnOnlineException.cast("请重新点击支付获取二维码");
        }
        //支付状态
        String status = payRecord.getStatus();
        if("601002".equals(status)){
            LearnOnlineException.cast("订单已支付，请勿重复支付。");
        }
        //构造sdk的客户端对象
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);//获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
//        alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        alipayRequest.setNotifyUrl("http://8g8kyk.natappfree.cc/orders/paynotify");//在公共参数中设置回跳和通知地址
        alipayRequest.setBizContent("{" +
                " \"out_trade_no\":\""+payRecord.getPayNo()+"\"," +
                " \"total_amount\":\""+payRecord.getTotalPrice()+"\"," +
                " \"subject\":\""+payRecord.getOrderName()+"\"," +
                " \"product_code\":\"QUICK_WAP_PAY\"" +
                " }");//填充业务参数
        String form = "";
        try {
            //请求支付宝下单接口,发起http请求
            form = client.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }


    /**
     * 查询支付结果
     *
     * @param payNo 支付订单号
     * @return 支付记录信息，包括支付状态、支付时间等
     * @throws IOException 如果查询支付结果失败，则抛出IOException异常
     */
    @ApiOperation("查询支付结果")
    @GetMapping("/payresult")
    @ResponseBody
    public PayRecordDto payresult(String payNo) throws IOException {
        //调用支付宝接口查询
        PayRecordDto payRecordDto = orderService.queryPayResult(payNo);
        return payRecordDto;
    }

    //接收支付宝通知
    @PostMapping("/paynotify")
    public void paynotify(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        boolean verify_result = AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, "RSA2");

        if (verify_result) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
            if (trade_status.equals("TRADE_FINISHED")) {//交易结束

            } else if (trade_status.equals("TRADE_SUCCESS")) {
                // 交易成功，保存订单信息
                PayStatusDto payStatusDto = new PayStatusDto();
                payStatusDto.setOut_trade_no(out_trade_no);
                payStatusDto.setTrade_no(trade_no);
                payStatusDto.setApp_id(APP_ID);
                payStatusDto.setTrade_status(trade_status);
                payStatusDto.setTotal_amount(total_amount);
                orderService.saveAliPayStatus(payStatusDto);
                log.debug("交易成功");
            }
            response.getWriter().write("success");
        } else {
            response.getWriter().write("fail");
        }
    }

}

