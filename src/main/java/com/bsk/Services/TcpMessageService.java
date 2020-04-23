package com.bsk.Services;

import javafx.concurrent.Task;
import javafx.util.Pair;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class TcpMessageService extends Task<Void> {

    @Value("${server.port}")
    private int port;

    private final Pair<String, String> encryptedContentAndSessionKey;

    public TcpMessageService(Pair<String, String> encryptedContentAndSessionKey) {
        this.encryptedContentAndSessionKey = encryptedContentAndSessionKey;
    }


    @Override
    protected Void call() throws Exception {
        updateMessage("Initiating...");
        try {
            Socket clientSocket = new Socket("localhost", port);

            if (clientSocket.isConnected()) {
                File contentFile = new File("content.txt");

                FileOutputStream f = new FileOutputStream(contentFile);
                ObjectOutputStream o = new ObjectOutputStream(f);
                o.writeObject(encryptedContentAndSessionKey);
                o.close();
                f.close();

                FileInputStream in = new FileInputStream(contentFile);
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF(contentFile.getName());
                byte[] buffer = new byte[4096];
                int readSize = 0, progress = 0;
                while ((readSize = in.read(buffer)) > 0) {
                    progress += readSize;
                    out.write(buffer, 0, readSize);
                    updateProgress(progress, contentFile.length());
                    Thread.sleep(2);

                    updateMessage(String.format("%.2f %%", (float)((float)progress / (float)contentFile.length() * 100.f)));
                }
                in.close();
                out.close();
                clientSocket.close();
            } else {
                throw new ConnectException();
            }
        } catch (ConnectException e) {
            System.out.println("Cannot connect.");
        }
        return null;
    }
}
