package optiTrack.examples;

import java.util.ArrayList;

import optiTrack.MocapDataClient;
import optiTrack.MocapLabeledMarkersListener;
import optiTrack.MocapMarker;

public class Example_MocapLabeledMarkerListener implements MocapLabeledMarkersListener
{
	int rigidBodyIdToTrack = 1;

	public Example_MocapLabeledMarkerListener(int interfaceId)
	{
		MocapDataClient mocapClient = new MocapDataClient(interfaceId);
		mocapClient.registerMocapLabeledMarkersListener(this);
	}

	public int getTrackingId()
	{
		return rigidBodyIdToTrack;
	}

	public static void main(String args[])
	{
		int interfaceId = -1;
		
		if(args.length > 0)
		{
			interfaceId = Integer.parseInt(args[0]);
		}
		
		if(interfaceId == 0)
			interfaceId = -1;
		
		Example_MocapLabeledMarkerListener mocapClientExample = new Example_MocapLabeledMarkerListener(interfaceId);
	}

	@Override
	public void updateMocapLabeledMarkers(ArrayList<MocapMarker> labeledMocapMarkers)
	{
		System.out.println("");
		System.out.println(">>>> START");
		System.out.println("Received " + labeledMocapMarkers.size() + " markers");
		System.out.println("Marker Data (labeled will display everything): ");
		for (MocapMarker marker : labeledMocapMarkers)
		{
			System.out.println(marker.toString());
		}
		System.out.println("<<<< END");
	}
}
