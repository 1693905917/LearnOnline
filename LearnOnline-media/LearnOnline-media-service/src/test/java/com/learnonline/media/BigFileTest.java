package com.learnonline.media;

import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @BelongsProject: LearnOnline
 * @BelongsPackage: com.learnonline.media
 * @Author: ASUS
 * @CreateTime: 2024-07-30  14:41
 * @Description: 大文件处理测试
 * @Version: 1.0
 */
public class BigFileTest {
    /**
     * 测试文件分块方法
     *
     * @throws IOException 如果发生IO异常
     */
    //测试文件分块方法
    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("e:/Java/IDEA_Practice/xueCheng-plus/develop/upload/Day5.mp4");
        String chunkPath = "e:/Java/IDEA_Practice/xueCheng-plus/develop/chunk/";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        //分块大小：1M
        long chunkSize = 1024 * 1024 * 5;
        //分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        System.out.println("分块总数："+chunkNum);
        //缓冲区大小
        byte[] b = new byte[1024];
        //使用RandomAccessFile访问文件：具有读写两个功能
        RandomAccessFile raf_read = new RandomAccessFile(sourceFile, "r");
        //分块
        for (int i = 0; i < chunkNum; i++) {//从第0号分块文件开始写数据
            //创建分块文件并且标记每个分块文件
            File file = new File(chunkPath + i);
            if(file.exists()){
                file.delete();
            }
            boolean newFile = file.createNewFile();//创建临时文件
            if (newFile) {
                //向分块文件中写数据
                RandomAccessFile raf_write = new RandomAccessFile(file, "rw");
                int len = -1;
                while ((len = raf_read.read(b)) != -1) {
                    raf_write.write(b, 0, len);
                    if (file.length() >= chunkSize) {//当写满了就不写了
                        break;
                    }
                }
                raf_write.close();
                System.out.println("完成分块"+i);
            }

        }
        raf_read.close();

    }

    //测试文件合并方法
    @Test
    public void testMerge() throws IOException {
        //块文件目录
        File chunkFolder = new File("e:/Java/IDEA_Practice/xueCheng-plus/develop/chunk/");
        //原始文件
        File originalFile = new File("e:/Java/IDEA_Practice/xueCheng-plus/develop/upload/Day5.mp4");
        //合并文件
        File mergeFile = new File("e:/Java/IDEA_Practice/xueCheng-plus/develop/upload/Day5_a.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        //创建新的合并文件
        mergeFile.createNewFile();
        //用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        //指针指向文件顶端
        raf_write.seek(0);
        //缓冲区
        byte[] b = new byte[1024];
        //分块列表
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序：我们要从0号分块开始一个一个写
        List<File> fileList = Arrays.asList(fileArray);
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        //合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);
            }
            raf_read.close();
        }
        raf_write.close();

        //校验文件
        try (

                FileInputStream fileInputStream = new FileInputStream(originalFile);
                FileInputStream mergeFileStream = new FileInputStream(mergeFile);

        ) {
            //取出原始文件的md5
            String originalMd5 = DigestUtils.md5Hex(fileInputStream);
            //取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }

        }
    }





}
