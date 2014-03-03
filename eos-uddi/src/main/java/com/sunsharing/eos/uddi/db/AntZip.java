package com.sunsharing.eos.uddi.db;

import java.io.*;
import org.apache.tools.zip.*;
import java.util.Enumeration;
/**
 *功能:zip压缩、解压(支持中文文件名)
 *说明:本程序通过使用Apache Ant里提供的zip工具org.apache.tools.zip实现了zip压缩和解压功能.
 *   解决了由于java.util.zip包不支持汉字的问题。
 *   使用java.util.zip包时,当zip文件中有名字为中文的文件时,
 *   就会出现异常:"Exception  in thread "main " java.lang.IllegalArgumentException
 *               at   java.util.zip.ZipInputStream.getUTF8String(ZipInputStream.java:285)
 *注意:
 *   1、使用时把ant.jar放到classpath中,程序中使用import org.apache.tools.zip.*;
 *   2、Apache Ant 下载地址:[url]http://ant.apache.org/[/url]
 *   3、Ant ZIP API:[url]http://www.jajakarta.org/ant/ant-1.6.1/docs/mix/manual/api/org/apache/tools/zip/[/url]
 *   4、本程序使用Ant 1.7.1 中的ant.jar
 *
 *仅供编程学习参考.
 *
 *@author Winty
 *@date   2008-8-3
 *@Usage:
 *   压缩:java AntZip -zip "directoryName"
 *   解压:java AntZip -unzip "fileName.zip"
 */

public class AntZip{
    private ZipFile         zipFile;
    private ZipOutputStream zipOut;     //压缩Zip
    private ZipEntry        zipEntry;
    private static int      bufSize;    //size of bytes
    private byte[]          buf;
    private int             readedBytes;

    public AntZip(){
        this(512);
    }

    public AntZip(int bufSize){
        this.bufSize = bufSize;
        this.buf = new byte[this.bufSize];
    }

    //压缩文件夹内的文件
    public void doZip(String zipDirectory,OutputStream out){//zipDirectoryPath:需要压缩的文件夹名
        File file;
        File zipDir;

        zipDir = new File(zipDirectory);
        String zipFileName = zipDir.getName() + ".zip";//压缩后生成的zip文件名

        try{
            this.zipOut = new ZipOutputStream(new BufferedOutputStream(out));
            handleDir(zipDir , this.zipOut);
            this.zipOut.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    //由doZip调用,递归完成目录文件读取
    private void handleDir(File dir , ZipOutputStream zipOut)throws IOException{
        FileInputStream fileIn;
        File[] files;

        files = dir.listFiles();

        if(files.length == 0){//如果目录为空,则单独创建之.
            //ZipEntry的isDirectory()方法中,目录以"/"结尾.
            String tmp = dir.toString();
            int index = dir.toString().replaceAll("\\\\","/").indexOf("zip/");
            if(index!=-1)
            {
                tmp = tmp.substring(index+37);
            }
            this.zipOut.putNextEntry(new ZipEntry(tmp + "/"));
            this.zipOut.closeEntry();
        }
        else{//如果目录不为空,则分别处理目录和文件.
            for(File fileName : files){
                //System.out.println(fileName);

                if(fileName.isDirectory()){
                    handleDir(fileName , this.zipOut);
                }
                else{
                    fileIn = new FileInputStream(fileName);
                    String tmp = fileName.toString().toString();
                    int index = fileName.toString().toString().replaceAll("\\\\","/").indexOf("zip/");
                    if(index!=-1)
                    {
                        tmp = tmp.substring(index+37);
                    }
                    this.zipOut.putNextEntry(new ZipEntry(tmp));

                    while((this.readedBytes = fileIn.read(this.buf))>0){
                        this.zipOut.write(this.buf , 0 , this.readedBytes);
                    }

                    this.zipOut.closeEntry();
                }
            }
        }
    }

    //解压指定zip文件
    public void unZip(String unZipfileName){//unZipfileName需要解压的zip文件名
        FileOutputStream fileOut;
        File file;
        InputStream inputStream;

        try{
            this.zipFile = new ZipFile(unZipfileName);

            for(Enumeration entries = this.zipFile.getEntries(); entries.hasMoreElements();){
                ZipEntry entry = (ZipEntry)entries.nextElement();
                file = new File(entry.getName());

                if(entry.isDirectory()){
                    file.mkdirs();
                }
                else{
                    //如果指定文件的目录不存在,则创建之.
                    File parent = file.getParentFile();
                    if(!parent.exists()){
                        parent.mkdirs();
                    }

                    inputStream = zipFile.getInputStream(entry);

                    fileOut = new FileOutputStream(file);
                    while(( this.readedBytes = inputStream.read(this.buf) ) > 0){
                        fileOut.write(this.buf , 0 , this.readedBytes );
                    }
                    fileOut.close();

                    inputStream.close();
                }
            }
            this.zipFile.close();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    //设置缓冲区大小
    public void setBufSize(int bufSize){
        this.bufSize = bufSize;
    }

    //测试AntZip类
    public static void main(String[] args)throws Exception{
        if(args.length==2){
            String name = args[1];
            AntZip zip = new AntZip();

//            if(args[0].equals("-zip"))
//                zip.doZip(name);
//            else if(args[0].equals("-unzip"))
//                zip.unZip(name);
        }
        else{
            System.out.println("Usage:");
            System.out.println("压缩:java AntZip -zip directoryName");
            System.out.println("解压:java AntZip -unzip fileName.zip");
            throw new Exception("Arguments error!");
        }
    }
}
