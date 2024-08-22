package com.zenzsol.allygain.data.db.cloudstore;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.zenzsol.allygain.data.db.TradeItemsDataHandler;
import com.zenzsol.allygain.data.dto.ui.AgItemUI;

@Component
public class AgCloudStoreDataHandler {

	private static String projectId = "dfdfd";
	private static String bucketName = "alfdfdfdfdfdm";

	@Autowired
	private TradeItemsDataHandler itemDataHandler;

	public AgCloudStoreDataHandler() {
	}

	public String uploadImageFileSellItem(AgItemUI agItemUI) throws Exception {
		//example content type you get is image/png
		String fileTyple = agItemUI.getImage().getContentType();
		int index = fileTyple.lastIndexOf("/");
		fileTyple = fileTyple.substring(index+1);
				
		String objectPath = "products/" + agItemUI.getCategory() + "/" + itemDataHandler.getNextCounterForFile() + "."
				+fileTyple ;
		Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
		BlobId blobId = BlobId.of(bucketName, objectPath);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		byte[] image = agItemUI.getImage().getBytes();
		storage.createFrom(blobInfo, new ByteArrayInputStream(image));
		return objectPath;
	}

	
	public String uploadImageFile(String contentType, String category, String subCat, byte[] image) throws Exception{
		//example content type you get is image/png

		int index = contentType.lastIndexOf("/");
		contentType = contentType.substring(index+1);
				
		String objectPath = category+"/" + subCat + "/" + itemDataHandler.getNextCounterForFile() + "."
				+contentType ;
		Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
		BlobId blobId = BlobId.of(bucketName, objectPath);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		
		storage.createFrom(blobInfo, new ByteArrayInputStream(image));
		return objectPath;
	}
}
