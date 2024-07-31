package com.learnonline.media;

import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.media
 * @Author: ASUS
 * @CreateTime: 2024-07-29  16:39
 * @Description: 测试MinIO的SDK
 * @Version: 1.0
 */
public class MinioTest {
    static MinioClient minioClient =
            MinioClient.builder()
                    .endpoint("http://192.168.101.65:9000")
                    .credentials("minioadmin", "minioadmin")
                    .build();

    //上传文件
    @Test
    public void upload() {
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(".mp4");
        //这个也是mimeType的默认值，如果你不知道你上传的文件类型，默认就是这个值
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;//通用mimeType，字节流
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        try {
            UploadObjectArgs testbucket = UploadObjectArgs.builder()
                    .bucket("testbucket")//确定用哪个桶
                    .filename("E:\\Java\\IDEA_Practice\\xueCheng-plus\\develop\\upload\\Day5.mp4")//我准备将本地的1mp4.temp文件上传
//                    .object("test001.mp4") //上传的文件存储到哪一个对象中，也就是test001.mp4对象中  这种方式是在桶下，也就是在根目录下直接存储文件
                    .object("001/Day5.mp4")//而这种方式是添加子目录，在001子目录下存储文件
                    .contentType(mimeType)//设置媒体文件类型的，上面写的《video/mp4》是我们自己指定的，默认根据扩展名确定文件内容类型，也可以指定
                    .build();
            minioClient.uploadObject(testbucket);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }

    @Test
    //删除文件
    public void delete() {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket("testbucket").object("001/Day5.mp4").build());
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
    }

    //查询文件
    @Test
    public void getFile() throws Exception {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket("testbucket").object("001/Day5.mp4").build();
        FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
        FileOutputStream outputStream = new FileOutputStream(new File("E:\\Java\\IDEA_Practice\\xueCheng-plus\\develop\\download\\Day5_a.mp4"));
        IOUtils.copy(inputStream, outputStream);
        //读取MINIO中的媒体文件的MD5值有两种方法：
        //第一种方法：获取远程流的MD5值，由于我们是通过网络来获取MINIO中的文件数据流，所以这个就叫做远程流，但是远程流是不稳定的，所以很有可能会让判断与本地下载的数据是否一致出现错误
        //String source_md5 = DigestUtils.md5Hex(inputStream);
        //第二种方法：直接就是本地上传的文件与从MINIO下载下来的文件进行对比
        FileInputStream fileInputStream1 = new FileInputStream(new File("E:\\Java\\IDEA_Practice\\xueCheng-plus\\develop\\upload\\Day5.mp4"));
        String source_md5 = DigestUtils.md5Hex(fileInputStream1);
        //读取下载以后在本地的媒体文件的MD5值
        FileInputStream fileInputStream = new FileInputStream(new File("E:\\Java\\IDEA_Practice\\xueCheng-plus\\develop\\download\\Day5_a.mp4"));
        String local_md5 = DigestUtils.md5Hex(fileInputStream);
        //如果下载到本地的文件MD5值与在MINIO中文件的MD5值一样就说明下载成功
        if(source_md5.equals(local_md5)){
            System.out.println("下载成功");
        }
    }

    //将分块文件上传至minio
    @Test
    public void uploadChunk(){
        String chunkFolderPath = "E:\\Java\\IDEA_Practice\\xueCheng-plus\\develop\\chunk\\";
        File chunkFolder = new File(chunkFolderPath);
        //分块文件
        File[] files = chunkFolder.listFiles();
        //将分块文件上传至minio
        for (int i = 0; i < files.length; i++) {
            try {
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().bucket("testbucket").object("chunk/" + i).filename(files[i].getAbsolutePath()).build();
                minioClient.uploadObject(uploadObjectArgs);
                System.out.println("上传分块成功"+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //注意：合并文件，minio默认的分块文件大小5M，要求分块文件最小5M
    @Test
    public void test_merge() throws Exception {
        // Stream.iterate(0, i -> ++i):从i=0开始，i++，当循环6次就结束
        List<ComposeSource> sources = Stream.iterate(0, i -> ++i)
                .limit(2)//这是分块文件的数
                // map将循环处理的东西映射成map括号内的
                .map(i -> ComposeSource.builder()
                        .bucket("testbucket")
                        .object("chunk/".concat(Integer.toString(i)))
                        .build())
                .collect(Collectors.toList());
        //指定合并后objectName等信息  merge01.mp4:合并后的文件
        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder().bucket("testbucket").object("merge01.mp4").sources(sources).build();
        minioClient.composeObject(composeObjectArgs);
    }
    //清除分块文件
    @Test
    public void test_removeObjects(){
        //合并分块完成将分块文件清除
        List<DeleteObject> deleteObjects = Stream.iterate(0, i -> ++i)
                .limit(2)
                .map(i -> new DeleteObject("chunk/".concat(Integer.toString(i))))
                .collect(Collectors.toList());

        RemoveObjectsArgs removeObjectsArgs = RemoveObjectsArgs.builder().bucket("testbucket").objects(deleteObjects).build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(removeObjectsArgs);
        results.forEach(r->{
            DeleteError deleteError = null;
            try {
                deleteError = r.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }



}


