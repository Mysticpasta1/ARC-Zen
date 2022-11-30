package com.mystic.arczen.utils;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetTemporaryLinkResult;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.*;

public class DropboxUploader {
    private static final String ACCESS_TOKEN = "sl.BUDkTYXVhAPwIPWpTFrpbMBfz6B7bS8J3fffQjkZTmn7p0HDGWgPGEqKRPhTCfumIPcUBBkHkUGBvgjIs8Iwj_hcvF86s92fsS-kWsagGj115lA-AAHBDCwVFwWTLGUYWJxQPAI";

    public static String uploadPdf(File pdf) throws DbxException, IOException {
        // Create Dropbox client
        DbxRequestConfig config = new DbxRequestConfig("dropbox/ARC-Zen");
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        // Get current account info
        FullAccount account = client.users().getCurrentAccount();
        System.out.println(account.getName().getDisplayName());

        // Get files and folder metadata from Dropbox root directory
        ListFolderResult result = client.files().listFolder("");
        while (true) {
            for (Metadata metadata : result.getEntries()) {
                System.out.println(metadata.getPathLower());
            }

            if (!result.getHasMore()) {
                break;
            }

            result = client.files().listFolderContinue(result.getCursor());
        }

        // Upload pdf to Dropbox
        try (InputStream in = new FileInputStream(pdf)) {
            FileMetadata metadata = client.files().uploadBuilder("/" + pdf.getName())
                    .uploadAndFinish(in);
        }

        DbxDownloader<FileMetadata> downloader = client.files().download("/" + pdf.getName());

        GetTemporaryLinkResult temporaryLinkResult = client.files().getTemporaryLink("/" + pdf.getName());
        return temporaryLinkResult.getLink();
    }
}