package optiTrack;

import java.util.ArrayList;

public class MocapMarkerSet
{
   private String dataSetName;
   private ArrayList<MocapMarker> markers = new ArrayList<MocapMarker>();

   public MocapMarkerSet(String dataSetName)
   {
      this.dataSetName = dataSetName;
   }

   public void addMarker(MocapMarker marker)
   {
      markers.add(marker);
   }

   public String getDataSetName()
   {
      return dataSetName;
   }

   public ArrayList<MocapMarker> getAssociatedMarkers()
   {
      return markers;
   }
}
