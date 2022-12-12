package optiTrack.examples;

import optiTrack.MocapDataClient;
import optiTrack.MocapMarker;
import optiTrack.MocapUnLabeledMarkersListener;

import java.util.ArrayList;

public class Example_MocapUnLabeledMarkerListener implements MocapUnLabeledMarkersListener
{
   int rigidBodyIdToTrack = 1;

   public Example_MocapUnLabeledMarkerListener(int interfaceId)
   {
      MocapDataClient mocapClient = new MocapDataClient(interfaceId);
      mocapClient.registerMocapUnLabeledMarkersListener(this);
   }

   public static void main(String args[])
   {
      int interfaceId = -1;

      if (args.length > 0)
      {
         interfaceId = Integer.parseInt(args[0]);
      }

      if (interfaceId == 0)
         interfaceId = -1;

      Example_MocapUnLabeledMarkerListener mocapClientExample = new Example_MocapUnLabeledMarkerListener(interfaceId);
   }

   public int getTrackingId()
   {
      return rigidBodyIdToTrack;
   }

   @Override
   public void updateMocapUnLabeledMarkers(ArrayList<MocapMarker> unLabeledMocapMarkers)
   {
      System.out.println("");
      System.out.println(">>>> START");
      System.out.println("Received " + unLabeledMocapMarkers.size() + " markers");
      System.out.println("Marker Data (unlabeled will only display position): ");
      for (MocapMarker marker : unLabeledMocapMarkers)
      {
         System.out.println(marker.toString());
      }
      System.out.println("<<<< END");
   }
}
