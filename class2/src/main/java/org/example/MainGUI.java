package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
//import java.awt.event.ActionListener;

public class MainGUI {

    private  JFrame jFrame;
    private JLabel textBoxLabel,downloadStatusLabel;

    private JTextField downloadUrlField;

    private JPanel jPanelOne;
    private JProgressBar downloadProgress;

    private JTable jTable;
    private JScrollPane jScrollPane;
    private final String[] COLUMNS = {"name","download date","download url"};

    MainGUI(){
        loadGUIComponents();
    }
    public void loadGUIComponents(){
        jFrame = new JFrame("Youtube Downloader");
        jFrame.setSize(800,700);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.getContentPane().setBackground(Color.WHITE);
        jFrame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Youtube Downloader", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setLayout(new BorderLayout());

        JLabel urlLabel = new JLabel("Enter URL:");
        urlLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        inputPanel.add(urlLabel, BorderLayout.WEST);

        downloadUrlField = new JTextField();
        downloadUrlField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputPanel.add(downloadUrlField, BorderLayout.CENTER);

        JButton downloadButton = new JButton("Download");
        downloadButton.setFont(new Font("Arial", Font.PLAIN, 20));
        downloadButton.addActionListener((ActionEvent e) -> {
            String userText = downloadUrlField.getText();
            saveDataToDatabase(userText);
            startDownload(userText);
            JOptionPane.showMessageDialog(jFrame,"User input : "+userText);
        });
        inputPanel.add(downloadButton, BorderLayout.EAST);

        topPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel progressPanel = new JPanel();
        progressPanel.setBackground(Color.WHITE);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        progressPanel.setLayout(new BorderLayout());

        JLabel progressLabel = new JLabel("Download Progress:");
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        progressPanel.add(progressLabel, BorderLayout.NORTH);

        downloadProgress = new JProgressBar();
        downloadProgress.setMaximum(100);
        downloadProgress.setMinimum(0);
        downloadProgress.setStringPainted(true);
        downloadProgress.setFont(new Font("Arial", Font.PLAIN, 20));
        progressPanel.add(downloadProgress, BorderLayout.CENTER);




        jFrame.add(topPanel, BorderLayout.NORTH);
        jFrame.add(progressPanel, BorderLayout.CENTER);

        DefaultTableModel defaultTableModel = new DefaultTableModel(getDownloadInformation(),COLUMNS);
        jTable = new JTable(defaultTableModel);
        jTable.setRowHeight(30);
        jTable.setFont(new Font("Arial", Font.PLAIN, 20));
        jTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        jScrollPane = new JScrollPane(jTable);
        jFrame.add(jScrollPane, BorderLayout.SOUTH);

        jFrame.setVisible(true);
    }


    private Object[][] getDownloadInformation() {
        Object[][] databaseList;
        //downloadInformation -> name, url, date
        try {
            java.util.List<DownloadInformation> downloadInformationList = DatabaseUtil.getAllDownloadInformation();
            databaseList = new Object[downloadInformationList.size()][3];
            List<Object[]> objects = downloadInformationList.stream()
                    .map(e -> new Object[]{e.getName(), e.getDate(), e.getDownloadUrl()})
                    .toList();

            for(int i=0;i<objects.size();i++){
                Object[] temp = objects.get(i);
                for(int j=0;j<3;j++){
                    databaseList[i][j]= temp[j];
                }
            }
            return databaseList;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void saveDataToDatabase(String url) {
        // nabin ->  nabin.substring(2) // abcd.com/uploads/something/abcd.txt ->12 -> url.substring(13)
        String name = url.substring(url.lastIndexOf("/")+1);
        String dateNow = LocalDateTime.now().toString();
        try {
            DatabaseUtil.saveToDatabase(name,dateNow,url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String[] rowsToBeInserted = {name,dateNow,url};
        DefaultTableModel defaultTableModel = (DefaultTableModel) jTable.getModel();
        defaultTableModel.addRow(rowsToBeInserted);
    }

    public void startDownload(String downlaodUrl) {
        // String a = "abcd";
        // String b = "abcd"; b = "cde"
        // url : https://somewebsite.com/downlaods/idm.exe .substring(0,4)

        StringBuffer userHome = new StringBuffer(System.getProperty("user.home"));
        StringBuffer fileSeparator = new StringBuffer(System.getProperty("file.separator"));
        StringBuffer downloadPath = userHome.append(fileSeparator).append("Downloads").append(fileSeparator);
        downloadPath.append(downlaodUrl.substring(downlaodUrl.lastIndexOf("/")+1));
        // someurl.com, downloads, 7:18, donwloading
        // ohterurl.com, downloads, 8:20, failed, success
//        DatabaseUtil.saveToDatabase(downlaodUrl,downloadPath, LocalDateTime.now());
        URL url;
        try {
            url = new URL(downlaodUrl);
            float fileTotalSize = getFileTotalSize(url);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(downloadPath.toString());
            byte[] b = new byte[1024];
            int count;
            int totalCount=0;
            while((count= bufferedInputStream.read(b,0,1024))!=-1){
                totalCount+= count;
                updateProgressBar(totalCount,fileTotalSize);
                fileOutputStream.write(b,0,count);
            }
        }catch(MalformedURLException malformedURLException){
            System.out.println("The url provided : "+downlaodUrl+ " is invalid");
        }catch (IOException ioException){
            System.out.println("The resource is not found");
        }
    }

    private void updateProgressBar(int totalCount, float fileTotalSize) {
        int percentageDownloaded = (int) ((totalCount/fileTotalSize) * 100);
        // total 32kb > 32 mb
        System.out.println("percentage downloaded "+(totalCount/fileTotalSize)*100);
        SwingUtilities.invokeLater(()-> {
            downloadProgress.setValue(percentageDownloaded);
            downloadProgress.setString(percentageDownloaded + "%");
            System.out.println("percentage downloaded " + percentageDownloaded);
            downloadStatusLabel.setText("Downloaded " + percentageDownloaded + "%");
        });
    }

    private float getFileTotalSize(URL url) {
        //expectedoutput = mb, kb, gb
        HttpURLConnection httpURLConnection;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            long length = httpURLConnection.getContentLengthLong();
            return length;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private float getSize(long length, String expectedOutput) {
        switch (expectedOutput){
            case "mb":
//                converttomb();
                break;
            case "gb":
                break;
            default:

        }
        return 0f;
    }

    public static void main(String[] args) throws MalformedURLException {
        String downloadUrl =  "https://az764295.vo.msecnd.net/stable/e2816fe719a4026ffa1ee0189dc89bdfdbafb164/VSCodeUserSetup-x64-1.75.0.exe";
//        PrintAllProperties();
        MainGUI mainGUI = new MainGUI();
//        getFileTotalSize(new URL(downloadUrl));
//        startDownload(downloadUrl);
    }

    private static void PrintAllProperties() {
        System.out.println(System.getProperty("user.home"));
        System.out.println(System.getProperty("file.separator"));
        System.out.println(System.getenv("OS"));
        System.out.println(System.getenv("NUMBER_OF_PROCESSORS"));
        Properties properties = System.getProperties();
        Enumeration<?> enumeration = properties.propertyNames();
        while(enumeration.hasMoreElements()){
            String property = enumeration.nextElement().toString();
            System.out.println(property +" "+System.getProperty(property));
        }
    }
}
