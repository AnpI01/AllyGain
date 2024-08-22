package com.zenzsol.allygain.data.db.firestore;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

public class FirestoreMain {

	private final Firestore db;

	public FirestoreMain(String projectId) throws Exception {

		FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder().setProjectId(projectId)
				.setCredentials(GoogleCredentials.getApplicationDefault()).build();
		db = firestoreOptions.getService();

	}

	public FirestoreMain() {

		Firestore db = FirestoreOptions.getDefaultInstance().getService();

		this.db = db;
	}

	Firestore getDb() {
		return db;
	}

}
