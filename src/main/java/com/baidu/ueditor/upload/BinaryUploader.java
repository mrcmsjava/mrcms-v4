package com.baidu.ueditor.upload;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItemFactory;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.marker.mushroom.utils.HttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public class BinaryUploader {


	/**
	 * 本地文件存储
	 * @param request 请求对象
	 * @param conf
	 * @return
	 */
	public static final State save(HttpServletRequest request,
								   Map<String, Object> conf) {
		FileItemInput fileStream = null;
		boolean isAjaxUpload = request.getHeader( "X_Requested_With" ) != null;

		if (!JakartaServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

		JakartaServletFileUpload upload = new JakartaServletFileUpload(DiskFileItemFactory.builder().get());

        if ( isAjaxUpload ) {
            upload.setHeaderCharset(Charset.defaultCharset());
        }

		try {
			FileItemInputIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				fileStream = iterator.next();

				if (!fileStream.isFormField())
					break;
				fileStream = null;
			}

			if (fileStream == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String savePath = (String) conf.get("savePath");
			String originFileName = fileStream.getName();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);

			String physicalPath = (String) conf.get("saveRootPath") + savePath;

			InputStream is = fileStream.getInputStream();
			State storageState = StorageManager.saveFileByInputStream(is,
					physicalPath, maxSize);
			is.close();

			String baseUrl = HttpUtils.getRequestURL(request);

			String savePathDir = PathFormat.format(savePath);

			if (storageState.isSuccess()) {
				storageState.putInfo("url", baseUrl + savePathDir);
				storageState.putInfo("src", savePathDir);
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (FileUploadException e) {
			log.error("", e);
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		} catch (IOException e) {
			log.error("", e);
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}


	/**
	 * 保存阿里云OSS
	 * @param request
	 * @param conf
	 * @return
	 */
	public static State saveAliyunOSS(HttpServletRequest request, Map<String, Object> conf, InputStream is) {
		FileItemInput fileStream = null;
		boolean isAjaxUpload = request.getHeader( "X_Requested_With" ) != null;

		if (is == null && !JakartaServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

		JakartaServletFileUpload upload = new JakartaServletFileUpload(DiskFileItemFactory.builder().get());

		if ( isAjaxUpload ) {
			upload.setHeaderCharset(Charset.defaultCharset());
		}

		try {
			FileItemInputIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				fileStream = iterator.next();

				if (!fileStream.isFormField())
					break;
				fileStream = null;
			}

			if (fileStream == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String savePath = (String) conf.get("savePath");
			String originFileName = fileStream.getName();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);
			savePath = savePath.replaceFirst("/","");


			String physicalPath = (String) conf.get("rootPath") + savePath;

			is = fileStream.getInputStream();

			/**
			 * 上传到阿里云：xhj添加
			 */
			//*******************开始***********************
			String fileName = new StringBuffer().append(new Date().getTime())
					.append(fileStream.getName().substring(fileStream.getName().indexOf("."))).toString();
			State storageState = null;
			try {
				Properties storageConfig = (Properties) request.getAttribute("storageConfig");

				String domain = storageConfig.getProperty("aliyunOss.domain");
				String endpoint = storageConfig.getProperty("aliyunOss.endpoint");
				String bucket = storageConfig.getProperty("aliyunOss.bucket");
				String accessKeyId = storageConfig.getProperty("aliyunOss.accessKeyId");
				String accessKeySecret = storageConfig.getProperty("aliyunOss.accessKeySecret");

				OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

				//此处"xxxx/yyyy/"+fileName,表示上传至阿里云中xxxx文件夹下的yyyy文件夹中，请修改为自己的路径即可
				PutObjectResult result = ossClient.putObject(bucket, savePath, is);
				ossClient.shutdown();
				storageState = new BaseState(true);
				storageState.putInfo( "size", result.getResponse().getContentLength() );
				storageState.putInfo( "title", fileName);
//				storageState = StorageManager.saveFileByInputStream(is, physicalPath, maxSize);
				storageState.putInfo("state", "SUCCESS");// UEDITOR的规则:不为SUCCESS则显示state的内容
				//注意：下面的url是返回到前端访问文件的路径，请自行修改
				storageState.putInfo("url", StringUtils.isNotBlank(domain) ? domain :
						String.format("https://%s.%s/%s", bucket, endpoint, savePath));
				storageState.putInfo("title", fileName);
				storageState.putInfo("original", fileName);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				storageState.putInfo("state", "文件上传失败!");
				storageState.putInfo("url","");
				storageState.putInfo("title", "");
				storageState.putInfo("original", "");
				//System.out.println("文件 "+fileName+" 上传失败!");
			}



			//********************结束**********************

			is.close();
/*if (storageState.isSuccess()) {
storageState.putInfo("url", PathFormat.format(savePath));
storageState.putInfo("type", suffix);
storageState.putInfo("original", originFileName + suffix);
}*/
			//System.out.println("storageState="+storageState);
			return storageState;
		} catch (FileUploadException e) {
			log.error("", e);
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		} catch (IOException e) {
			log.error("", e);
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	public static State saveAliyunOSSInputStream(HttpServletRequest request, Map<String, Object> conf,
												 InputStream is, String physicalPath) {

		try {

			if (is == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String originFileName = physicalPath;

			String savePath = (String) conf.get("savePath");
			String suffix = FileType.getSuffixByFilename(originFileName);

//			originFileName = originFileName.substring(0,
//					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);
			savePath = savePath.replaceFirst("/","");


//			String physicalPath = (String) conf.get("rootPath") + savePath;


			/**
			 * 上传到阿里云：xhj添加
			 */
			//*******************开始***********************

			State storageState = null;
			try {
				Properties storageConfig = (Properties) request.getAttribute("storageConfig");

				String domain = storageConfig.getProperty("aliyunOss.domain");
				String endpoint = storageConfig.getProperty("aliyunOss.endpoint");
				String bucket = storageConfig.getProperty("aliyunOss.bucket");
				String accessKeyId = storageConfig.getProperty("aliyunOss.accessKeyId");
				String accessKeySecret = storageConfig.getProperty("aliyunOss.accessKeySecret");

				OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

				//此处"xxxx/yyyy/"+fileName,表示上传至阿里云中xxxx文件夹下的yyyy文件夹中，请修改为自己的路径即可
				ossClient.putObject(bucket, savePath, is);
				ossClient.shutdown();

				storageState = StorageManager.saveFileByInputStream(is,
						physicalPath, maxSize);
				storageState.putInfo("state", "SUCCESS");// UEDITOR的规则:不为SUCCESS则显示state的内容
				//注意：下面的url是返回到前端访问文件的路径，请自行修改
				storageState.putInfo("url", StringUtils.isNotBlank(domain) ? domain :
						String.format("https://%s.%s/%s", bucket, endpoint, savePath));
				storageState.putInfo("title", originFileName);
				storageState.putInfo("original", originFileName);
			} catch (Exception e) {
				log.error("", e);
				storageState.putInfo("state", "文件上传失败!");
				storageState.putInfo("url","");
				storageState.putInfo("title", "");
				storageState.putInfo("original", "");
				//System.out.println("文件 "+fileName+" 上传失败!");
			}
			//********************结束**********************

			is.close();
/*if (storageState.isSuccess()) {
storageState.putInfo("url", PathFormat.format(savePath));
storageState.putInfo("type", suffix);
storageState.putInfo("original", originFileName + suffix);
}*/
			//System.out.println("storageState="+storageState);
			return storageState;
		} catch (IOException e) {
			log.error("", e);
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}
}
