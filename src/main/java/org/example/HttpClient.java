package org.example;

import jdk.jfr.Category;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class HttpClient {
    //realizarea conexiunilor HTTPS fără a verifica certificatele serverului
    public static HttpsURLConnection openConnection(URL url) {
        try {
            // crearea SSLContext cu un trust manager care acceptă toate certificatele
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new TrustAllCertificates()}, null);//se initializează SSLContext cu un TrustManager care acceptă toate certificatele
            // setare SSLContext pe conexiunea HTTPS
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            // eschiderea conexiunii HTTP și returnarea acesteia
            return (HttpsURLConnection) url.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = openConnection(url);
            connection.setRequestMethod("GET");
            // citire rasouns de la server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            System.out.println("Răspunsul de la server:");
            System.out.println(response.toString());
            // inchidere conexiune
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = openConnection(url);
            connection.setRequestMethod("DELETE");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Obiectul a fost sters cu succes.");
            } else {
                System.out.println("Eroare la stergerea obiectului. Cod de stare: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postRequest(String urlString, String requestBody) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = openConnection(url);
            connection.setRequestMethod("POST");
            // setare tip de conținut al cererii (JSON)
            connection.setRequestProperty("Content-Type", "application/json");
            //permiterea datelor in body
            connection.setDoOutput(true);

            // trimitere corp cerere către server
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            System.out.println("Răspunsul de la server:");
            System.out.println(response.toString());

            // Verifică codul de stare al răspunsului de la server
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Obiectul a fost creat cu succes.");
            } else {
                System.out.println("Eroare la crearea obiectului. Cod de stare: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void putRequest(String urlString, String requestBody) {
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = openConnection(url);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Obiectul a fost actualizat cu succes.");
            } else {
                System.out.println("Eroare la actualizarea obiectului. Cod de stare: " + responseCode);
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void vizualizareCategorii() {
        getRequest("https://localhost:5001/api/Category/categories");
    }
    public static void vizualizareCategorieDupaId(BufferedReader reader) throws IOException {
        System.out.println("Introduceți numărul categoriei:");
        String categoryId = reader.readLine();
        getRequest("https://localhost:5001/api/Category/categories"+"/" + categoryId);
    }
    public static void vizualizareProdusDupaIdCategoriei(BufferedReader reader) throws IOException {
        //extragere produse a unei categorii specifice
        System.out.println("Introduceți id categoriei:");
        String categoryId=reader.readLine();
        getRequest("https://localhost:5001/api/Category/categories/"+ categoryId+"/products");
    }
    public static void vizualizareIdCategorieDupaNume(BufferedReader reader) throws IOException {
        System.out.println("Introduceți numele categoriei:");
        String nume=reader.readLine();
        getRequest("https://localhost:5001/api/Category/categories/search?categoryName="+nume);
    }
    public static void creareCategorieNoua(BufferedReader reader) throws IOException {
        System.out.println("Introduceți titlul noii categorii:");
        String newCategoryTitle= reader.readLine();
        String requestBody = "{\"title\": \""+newCategoryTitle+"\"}";
        postRequest("https://localhost:5001/api/Category/categories",requestBody);
    }
    public static void creareProdusDupaIdCategorie(BufferedReader reader) throws IOException {
        System.out.println("Numar categorie:");
        String idCategory = reader.readLine();
        System.out.println("Titlul/numele produsului:");
        String titluProdus = reader.readLine();
        System.out.println("Pret produs:");
        String pretProdus = reader.readLine();
        String requestBody="{\"id\":0,\"title\":\""+titluProdus+"\",\"price\":"+pretProdus+",\"categoryId\":"+idCategory+"}";
        postRequest("https://localhost:5001/api/Category/categories/"+idCategory+"/products",requestBody);
    }
    public static void stergereCategorie(BufferedReader reader) throws IOException {
        System.out.println("Introduceti id-ul categoriei pe care doriti sa o stergeti");
        String categoryToDelete = reader.readLine();
        deleteRequest("https://localhost:5001/api/Category/categories/"+ categoryToDelete);
    }

    public static void actualizareCategorie(BufferedReader reader) throws IOException {
        System.out.println("Introduceți id categoriei pe care doriti sa o actualizati:");
        String idCategory= reader.readLine();
        System.out.println("Introduceți un titlu nou:");
        String titluNou= reader.readLine();
        String requestBody="{\"id\":"+idCategory+",\"name\":\""+titluNou+"\",\"itemsCount\":0}";
        putRequest("https://localhost:5001/api/Category/"+idCategory,requestBody);
    }


    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Alegeți o opțiune:");
            System.out.println("1. Enumerare lista de categorii");
            System.out.println("2. Afișare detalii despre o categorie după id");
            System.out.println("3. Afișare detalii despre o categorie după nume");
            System.out.println("4. Creare categorie nouă");
            System.out.println("5. Ștergere categorie");
            System.out.println("6. Modificare titlu categorie");
            System.out.println("7. Creare produse noi într-o categorie");
            System.out.println("8. Afișare listă produse dintr-o categorie");
            System.out.println("0. Ieșire");

            int option = Integer.parseInt(reader.readLine());

            switch (option) {
                case 0:
                    System.out.println("Ieșire...");
                    return;
                case 1:
                    vizualizareCategorii();
                    break;
                case 2:
                    vizualizareCategorieDupaId(reader);
                    break;
                case 3:
                    vizualizareIdCategorieDupaNume(reader);
                    break;
                case 4:
                    creareCategorieNoua(reader);
                    break;
                case 5:
                    stergereCategorie(reader);
                    break;
                case 6:
                    actualizareCategorie(reader);
                    break;
                case 7:
                    creareProdusDupaIdCategorie(reader);
                    break;
                case 8:
                    vizualizareProdusDupaIdCategoriei(reader);
                    break;
                default:
                    System.out.println("Optiune invalida");
                    break;
            }
        }
    }
}


