package com.example.asami.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
   ArrayList<String> celebImg ;
    ArrayList<String> celebNames ;
    int celebNext;
    Bitmap currentImage;
    ImageView imageView ;
    int correctAnswer;
    int incorrect;
    int tag;
    String name;
    String [] answers = new String[4];
    DownloadUrl task;
    Button button;
    Button button1;
    Button button2;
    Button button3;
    Pattern p ;
    Matcher m;
    DownloadImage taskImage;
    String result;
    String [] modifiedCode;
    Random random;

       public void generate()
          {
              //will contain the result


              //to split the web source code to contain only the imgsource that we need wihout including the ones at the end of the page
              // so we split it //<div class="sidebarContainer">

              //FOR IMAGE SOURCES ONLY

                  celebNext = random.nextInt(celebImg.size());

                  try {
                      currentImage = taskImage.execute(celebImg.get(celebNext)).get();

                      imageView.setImageBitmap(currentImage);

                      correctAnswer=random.nextInt(4);
                      incorrect = random.nextInt(4);
                      for(int i =0; i<=3;i++)
                      {
                          if(correctAnswer == i)
                          {

                              answers[i] = celebNames.get(celebNext);
                              System.out.println(answers[i]);
                          }else
                          {
                              incorrect = random.nextInt(celebImg.size());
                              while(incorrect== celebNext)
                              {
                                  incorrect = random.nextInt(celebImg.size());

                              }


                              answers[i] = celebNames.get(incorrect);
                              System.out.println(answers[i]);
                          }



                      }

                      button.setText(answers[0]);
                      button1.setText(answers[1]);
                      button2.setText(answers[2]);
                      button3.setText(answers[3]);

                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  } catch (ExecutionException e) {
                      e.printStackTrace();
                  }


              }


      public void checkCeleb (View view)
         {

                tag=Integer.parseInt(view.getTag().toString());

                 if (tag == correctAnswer)

                     {

                         Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();

                     }else
                         {

                             Toast.makeText(getApplicationContext(), "Wrong its " + answers[correctAnswer] , Toast.LENGTH_SHORT).show();
                         }


                         generate();

         }
     //downloading the image

        public class DownloadImage extends  AsyncTask<String,Void,Bitmap>
           {


               @Override
               protected Bitmap doInBackground(String... urls) {

                   URL url ;
                   HttpURLConnection connect;
                   Log.i("url",urls[0]);
                   try {
                       url = new URL(urls[0]);
                       connect = (HttpURLConnection) url.openConnection();

                       //download the hall image at once in the input strean

                       InputStream stream = connect.getInputStream();

                       //now decode the image using bitmapfactory.decode

                       Bitmap decodedImage = BitmapFactory.decodeStream(stream);

                      return  decodedImage;

                   } catch (MalformedURLException e) {
                       e.printStackTrace();
                   }
                     catch (IOException e)
                       {
                          e.printStackTrace();
                       }
                   return null;
               }
           }
      //we gonna download the hall web source for the page
       public class DownloadUrl extends AsyncTask<String , Void , String>
          {


            @Override
              protected String doInBackground (String... urls)
                 {
                     StringBuilder code = new StringBuilder();

                     URL url;


                     HttpURLConnection connect;

                     try {
                         url = new URL( urls[0]);
                         connect = (HttpURLConnection) url.openConnection();
                         //input stream which will contain data
                         InputStream stream = connect.getInputStream() ;

                         InputStreamReader reader = new InputStreamReader(stream);

                         //reades the first data
                         int data = reader.read();

                         while(data!=-1)
                            {

                                 //it may be char or int  int web code , either way ill cast it to char to add it to code
                                 char current = (char) data;
                                code.append(((char)data));
                                //next charcter
                                data = reader.read();


                            }


                           return  code.toString();


                     } catch (MalformedURLException e)
                        {
                         e.printStackTrace();
                        }

                     catch (IOException e)
                        {
                       e.printStackTrace();
                        }

                     catch (Exception e)
                        {
                         e.printStackTrace();
                        }


                     return null;
                 }


          }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         celebImg = new ArrayList<String>();
         celebNames = new ArrayList<String>();
         task = new DownloadUrl();
         taskImage = new DownloadImage();
        imageView = (ImageView) findViewById(R.id.imageView2);
        result =null;
        random = new Random();

        button =  (Button) findViewById(R.id.b1);
        button1 = (Button) findViewById(R.id.b2);
        button2 = (Button) findViewById(R.id.b3);
        button3= (Button) findViewById(R.id.b4);

        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            modifiedCode = result.split("<div class=\"sidebarContainer\">");

            p = Pattern.compile("<img src=\"(.*?)\"");
            //first one in the array
            m= p.matcher(modifiedCode[0]);

            while(m.find())
            {
                celebImg.add(m.group(1));
            }

            //FOR NAMES ONLY
            p = Pattern.compile("alt=\"(.*?)\"/");
            m = p.matcher(modifiedCode[0]);

            while(m.find())
            {

                celebNames.add(m.group(1));
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        generate();

    }
}
