package optiTrack;

import java.io.*;

public class MocapFileLoader
{
   public MocapFileLoader() throws IOException
   {
      String logFile = System.getProperty("user.home") + File.separator + ".ihmc" + File.separator + "logs" + File.separator + "mocapData.txt";
      File file = new File(logFile);
      BufferedReader dataFileReader = new BufferedReader(new FileReader(file));

      /* magic number in MocapDataClient */
      byte[] buffer = new byte[9500];

      for (int i = 0; i < buffer.length; i++)
      {
         buffer[i] = (byte) dataFileReader.read();
      }
   }

   public static void main(String[] args) throws IOException
   {
      new MocapFileLoader();
   }
}
