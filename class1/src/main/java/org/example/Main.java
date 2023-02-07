package org.example;

import com.github.axet.vget.VGet;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //m2 in the user//riteshrjal/m2 for maven
        // System.out.println("Hello world!");
        Scanner sc = new Scanner(System.in);
        System.out.println("enter youtube url");
        String url = sc.next();

        VGet vGet;

        try {

            String path = "D:\\Newfolder";
            URL url1 = new URL(url);

            VGet v = new VGet(url1, new File(path));
            v.download();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e ){
            System.out.println("error while downloading");

        }

        }
    }

