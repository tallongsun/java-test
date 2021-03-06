package com.dl.resteasy.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.Header;
import org.apache.james.mime4j.parser.Field;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {
	private final String UPLOADED_FILE_PATH = "src/main/resource/static/files/";
	
	@Override
	public Response uploadFile(MultipartFormDataInput fileInput) {
		String fileName = "";
		Map<String, List<InputPart>> uploadForm = fileInput.getFormDataMap();
		List<InputPart> inputParts = uploadForm.get("uploadedFile");
		// if a page include 2 file and 2 form field, there are 4 input part?
		for (InputPart inputPart : inputParts) {
			try {
				fileName = getFileName(inputPart);

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				// TODO big file may cause out of memory, use code like: while(inputStream.read){file.write}
				byte[] bytes = IOUtils.toByteArray(inputStream);

				// constructs upload file path
				fileName = UPLOADED_FILE_PATH + fileName;

				writeFile(bytes, fileName);

				System.out.println("Done");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return Response.status(200)
			    .entity("uploadFile is called, Uploaded file name : " + fileName).build();
	}
	
	@Override
	public Response downloadFile(String fileName) {

		CSVFormat format = CSVFormat.DEFAULT.withHeader("A","B");
		try(FileWriter fileWriter = new FileWriter(UPLOADED_FILE_PATH+fileName);
			CSVPrinter printer = new CSVPrinter(fileWriter,format)){
			for(int i=0;i<10;i++){
				printer.printRecord(Arrays.asList("A"+i,"B"+i));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return Response.status(200)
				.entity("downloadFile is called, Downloaded file name : " + fileName).build();
//		File file = new File(UPLOADED_FILE_PATH+fileName);
//		return Response.ok(file).header("Content-Disposition", "filename="+fileName).build();
	}
	
	public String getFileName(MultivaluedMap<String, String> header) {

		String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

		for (String filename : contentDisposition) {
			if ((filename.trim().startsWith("filename"))) {

				String[] name = filename.split("=");

				String finalFileName = name[1].trim().replaceAll("\"", "");
				return finalFileName;
			}
		}
		return "unknown";
	}

	/**
	 * 解决中文文件名问题
	 * @param inputPart
	 * @return
	 */
	public String getFileName(InputPart inputPart){
        String fileName = null;
        try{
            java.lang.reflect.Field field = inputPart.getClass().getDeclaredField("bodyPart");
            field.setAccessible(true);
            BodyPart bodyPart = (BodyPart)field.get(inputPart);
            Header header = bodyPart.getHeader();
            Field[] fields = header.getFields().toArray(new Field[0]);
            String[] contentDisposition = fields[0].getRaw().toString().split(";");

            for (String filename : contentDisposition) {
                if (filename.trim().startsWith("filename")) {
                    String[] name = filename.split("=");
                    fileName = name[1].trim().replaceAll("\"", "");
                }
            }
        }catch (Exception e){

        }
        return fileName;
    }
	
	private void writeFile(byte[] content, String filename) throws IOException {

		File file = new File(filename);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}


}
