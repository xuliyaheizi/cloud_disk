package com.zhulin.service.impl;

import com.zhulin.service.HdfsService;
import com.zhulin.utils.DiskUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Author:ZHULIN
 * @Date: 2022/7/4
 * @Description:
 */
@Service
public class HdfsServiceImpl implements HdfsService {
    @Value("${hdfs.path}")
    private String path;
    @Value("${hdfs.username}")
    private String username;
    @Value("${hdfs.nameservices}")
    private String nameservices;
    @Value("${hdfs.namenodes}")
    private String namenodes;
    @Value("${hdfs.master}")
    private String master;
    @Value("${hdfs.node1}")
    private String node1;
    @Value("${hdfs.provider}")
    private String provider;

    private final int bufferSize = 1024 * 1024 * 64;

    @Override
    public Configuration getConfiguration() {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", path);
        conf.set("dfs.nameservices", nameservices);
        conf.set("dfs.ha.namenodes.yc", namenodes);
        conf.set("dfs.namenode.rpc-address.yc.nn1", master);
        conf.set("dfs.namenode.rpc-address.yc.nn2", node1);
        conf.set("dfs.client.failover.proxy.provider.yc", provider);
        return conf;
    }

    @Override
    public FileSystem getFileSystem() {
        try {
            Configuration conf = getConfiguration();
            return FileSystem.get(new URI(path), conf, username);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getConfigurationInfoAsMap() {
        //获取文件系统
        FileSystem fs = getFileSystem();
        Configuration conf = fs.getConf();
        //构造conf的迭代器
        Iterator<Map.Entry<String, String>> iterator = conf.iterator();
        Map<String, String> map = new HashMap<>(16);
        try {
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                map.put(entry.getKey(), entry.getKey());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    @Override
    public List<Map<String, String>> listStatus(String path) {
        Path srcPath = new Path(path);
        FileSystem fs = getFileSystem();
        List<Map<String, String>> resultList = new ArrayList<>();
        try {
            FileStatus[] fileStatuses = fs.listStatus(srcPath);
            if (fileStatuses == null || fileStatuses.length <= 0) {
                return null;
            }
            for (FileStatus file : fileStatuses) {
                Map<String, String> map = fileStatusToMap(file);
                resultList.add(map);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return resultList;
    }

    @Override
    public boolean existFile(String path) {
        boolean isExists = false;
        FileSystem fs = getFileSystem();
        try {
            Path srcPath = new Path(path);
            isExists = fs.exists(srcPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return isExists;
    }

    @Override
    public boolean mkdir(String path) {
        //判断目录是否存在 存在则不创建
        if (existFile(path)) {
            return false;
        }
        FileSystem fs = getFileSystem();
        Path p = new Path(path);
        boolean mkdirs = false;
        try {
            mkdirs = fs.mkdirs(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return mkdirs;
    }

    @Override
    public List<Map<String, Object>> readPathInfo(String path) {
        //目标路径
        Path p = new Path(path);
        FileSystem fs = getFileSystem();
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            FileStatus[] listStatus = fs.listStatus(p);
            if (listStatus == null || listStatus.length < 0) {
                return null;
            }
            for (FileStatus status : listStatus) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("filePath", status.getPath());
                map.put("fileStatus", status.toString());
                list.add(map);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

    @Override
    public void createFile(String path, MultipartFile file) {
        FileSystem fs = getFileSystem();
        FSDataOutputStream outputStream = null;
        try {
            String fileName = file.getOriginalFilename();
            //上传时默认当前目录，后面自动拼接文件的目录
            Path newPath = null;
            if ("/".equals(path)) {
                newPath = new Path(path + fileName);
            } else {
                newPath = new Path(path + "/" + fileName);
            }
            //打开一个输出流
            outputStream = fs.create(newPath);
            outputStream.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                outputStream.close();
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public StringBuffer readFile(String path) {
        Path p = new Path(path);
        FileSystem fs = getFileSystem();
        FSDataInputStream inputStream = null;
        try {
            inputStream = fs.open(p);
            //防止中文乱码
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String lineTxt = "";
            StringBuffer sb = new StringBuffer();
            while ((lineTxt = reader.readLine()) != null) {
                sb.append(lineTxt);
                sb.append("<br>");
            }
            return sb;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Map<String, String>> listStatus(int type) {
        //根路径  查找磁盘所有文件
        String path = "/";
        Path srcPath = new Path(path);
        List<Map<String, String>> returnList = new ArrayList<>();
        String reg = null;
        if (type == 1) {
            reg = ".+(.jpeg|.jpg|.bmp|.gif)$";
        } else if (type == 2) {
            reg = ".+(.txt|.rtf|.doc|.docx|.xls|.xlsx|.html|.htm|.xml)$";
        } else if (type == 3) {
            reg = ".+(.mp4|.avi|.wmv)$";
        } else if (type == 4) {
            reg = ".+(.mp3|.wav)$";
        } else if (type == 5) {
            reg = "^\\S+\\.*$";
        }
        //正则表达式
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        search(srcPath, returnList, pattern);
        return returnList;
    }

    private void search(Path srcPath, List<Map<String, String>> list, Pattern pattern) {
        FileSystem fs = getFileSystem();
        try {
            FileStatus[] fileStatuses = fs.listStatus(srcPath);
            if (fileStatuses != null && fileStatuses.length > 0) {
                for (FileStatus status : fileStatuses) {
                    boolean result = status.isFile();
                    if (!result) {
                        //是目录  则递归
                        search(status.getPath(), list, pattern);
                    } else {
                        //是文件，则判断类型
                        boolean fileClasses = pattern.matcher(status.getPath().getName()).find();
                        if (fileClasses) {
                            Map<String, String> map = this.fileStatusToMap(status);
                            list.add(map);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Map<String, String> getFileInfo(String path) {
        Path srcPath = new Path(path);
        FileSystem fs = getFileSystem();
        try {
            FileStatus fileStatus = fs.getFileStatus(srcPath);
            return fileStatusToMap(fileStatus);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public List<Map<String, String>> listFile(String path) {
        //获取目标路径
        Path srcPath = new Path(path);
        FileSystem fs = getFileSystem();
        List<Map<String, String>> returnList = null;
        try {
            //递归找到所有文件 true-递归的
            RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(srcPath, true);
            returnList = new ArrayList<>();
            while (listFiles.hasNext()) {
                LocatedFileStatus next = listFiles.next();
                String fileName = next.getPath().getName();
                Path filePath = next.getPath();
                Map<String, String> map = new HashMap<>(16);
                map.put("fileName", fileName);
                map.put("filePath", filePath.toString());
                returnList.add(map);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return returnList;
    }

    @Override
    public boolean renameFile(String oldName, String newName) {
        //原文件目标路径
        Path oldPath = new Path(oldName);
        //重命名目标路径
        Path newPath = new Path(newName);
        FileSystem fs = getFileSystem();
        boolean rename = false;
        try {
            rename = fs.rename(oldPath, newPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return rename;
    }

    @Override
    public boolean deleteFile(String path) {
        //获取目标路径
        Path srcPath = new Path(path);
        FileSystem fs = getFileSystem();
        //true表示递归删除目录
        boolean delete = false;
        try {
            delete = fs.delete(srcPath, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return delete;
    }

    @Override
    public ResponseEntity<InputStreamResource> downLoadFile(String path, String fileName) {
        Path srcPath = new Path(path);

        FileSystem fs = getFileSystem();
        FSDataInputStream open = null;
        try {
            open = fs.open(srcPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return DiskUtil.downLoadFile(open, fileName);
    }

    @Override
    public ResponseEntity<byte[]> downLoadDirectory(String path, String fileName) {
        //获取对象
        ByteArrayOutputStream outputStream = null;
        ZipOutputStream zos = null;
        FileSystem fs = getFileSystem();
        byte[] bs = null;
        try {
            //内存输出流
            outputStream = new ByteArrayOutputStream();
            //压缩输出流
            zos = new ZipOutputStream(outputStream);
            compress(path, zos, fs);
            bs = outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                zos.close();
                outputStream.close();
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return DiskUtil.downLoadDirectoryFile(bs, DiskUtil.genFileName(fileName));
    }

    private void compress(String baseDir, ZipOutputStream zipOutputStream, FileSystem fileSystem) {
        try {
            FileStatus[] fileStatuses = fileSystem.listStatus(new Path(baseDir));
            String[] strs = baseDir.split("/");
            String lastName = strs[strs.length - 1];
            for (FileStatus fileStatus : fileStatuses) {
                String name = fileStatus.getPath().toString();
                name = name.substring(name.indexOf("/" + lastName));
                if (fileStatus.isFile()) {
                    Path filePath = fileStatus.getPath();
                    FSDataInputStream inputStream = fileSystem.open(filePath);
                    zipOutputStream.putNextEntry(new ZipEntry(name.substring(1)));
                    IOUtils.copyBytes(inputStream, zipOutputStream, bufferSize);
                    inputStream.close();
                } else {
                    zipOutputStream.putNextEntry(new ZipEntry(fileStatus.getPath().getName() + "/"));
                    compress(fileStatus.getPath().toString(), zipOutputStream, fileSystem);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean copyFile(String sourcePath, String targetPath) {
        //原始文件路径
        Path oldPath = new Path(sourcePath);
        //目标文件路径
        Path newPath = new Path(targetPath);

        FileSystem fs = getFileSystem();
        FSDataInputStream inputStream = null;
        FSDataOutputStream outputStream = null;
        try {
            inputStream = fs.open(oldPath);
            outputStream = fs.create(newPath);
            //TODO:IOUtil工具类文件复制
            IOUtils.copyBytes(inputStream, outputStream, bufferSize, false);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                inputStream.close();
                outputStream.close();
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public byte[] openFileToBytes(String path) {
        Path srcPath = new Path(path);
        byte[] result = null;
        FileSystem fs = getFileSystem();
        FSDataInputStream inputStream = null;
        InputStream iis = null;
        ByteArrayOutputStream baos = null;
        try {
            inputStream = fs.open(srcPath);
            iis = inputStream.getWrappedStream();
            baos = new ByteArrayOutputStream();
            byte[] bs = new byte[10 * 1024];
            int length = 0;
            while ((length = iis.read(bs, 0, bs.length)) != -1) {
                baos.write(bs, 0, length);
            }
            baos.flush();
            result = baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
                iis.close();
                baos.close();
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @Override
    public BlockLocation[] getFileBlockLocations(String path) {
        Path srcPath = new Path(path);

        FileSystem fs = getFileSystem();
        BlockLocation[] fileBlockLocations = new BlockLocation[0];
        try {
            FileStatus fileStatus = fs.getFileStatus(srcPath);
            fileBlockLocations = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileBlockLocations;
    }

    @Override
    public void upLoadFile(String path, String upLoadPath) {
        //上传路径
        Path clientPath = new Path(path);
        //目标路径
        Path serverPath = new Path(upLoadPath);

        FileSystem fs = getFileSystem();
        //调用文件系统的文件复制方法
        try {
            fs.copyFromLocalFile(clientPath, serverPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 将fileStatus转为一个map
     * @param fileStatus
     * @return
     */
    private Map<String, String> fileStatusToMap(FileStatus fileStatus) {
        Map<String, String> map = new HashMap<>(16);
        Path p = fileStatus.getPath();
        map.put("fileName", p.getName());
        String filePath = p.toUri().toString();
        map.put("filePath", filePath);
        String relativePath = filePath.substring(this.path.length());
        map.put("relativePath", relativePath);
        map.put("parentPath", p.getParent().toUri().toString().substring(this.path.length()));
        map.put("owner", fileStatus.getOwner());
        map.put("group", fileStatus.getGroup());
        map.put("isFile", fileStatus.isFile() + "");
        map.put("duplicates", fileStatus.getReplication() + "");
        map.put("size", DiskUtil.formatFileSize(fileStatus.getLen()));
        map.put("rights", fileStatus.getPermission().toString());
        map.put("modifyTime", DiskUtil.formatTime(fileStatus.getModificationTime()));
        return map;
    }
}
