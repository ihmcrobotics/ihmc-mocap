package optiTrack.examples;

import java.util.ArrayList;

import optiTrack.MocapDataClient;
import optiTrack.MocapMarker;
import optiTrack.MocapMarkerSet;
import optiTrack.MocapMarkerSetListener;

public class Example_MocapMarkerSetListener implements MocapMarkerSetListener
{
	int rigidBodyIdToTrack = 1;

	public Example_MocapMarkerSetListener(int interfaceId)
	{
		MocapDataClient mocapClient = new MocapDataClient(interfaceId);
		mocapClient.registerMocapMarkerSetListener(this);
	}

	public int getTrackingId()
	{
		return rigidBodyIdToTrack;
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

		Example_MocapMarkerSetListener mocapClientExample = new Example_MocapMarkerSetListener(interfaceId);
	}

	@Override
	public void updateMocapMarkerSets(ArrayList<MocapMarkerSet> markerSets)
	{
		System.out.println("");
		System.out.println(">>>> START");
		System.out.println("Received " + markerSets.size() + " marker sets");
		for (MocapMarkerSet set : markerSets)
		{
			System.out.println("Received a Marker Set with name: " + set.getDataSetName());
			System.out.println("The set has " + set.getAssociatedMarkers().size() + " markers");
			System.out.println("Marker Data: ");

			for (MocapMarker marker : set.getAssociatedMarkers())
			{
				System.out.println("ID: " + marker.getId() + " Position: " + marker.getPosition());
			}
		}
		System.out.println("<<<< END");
	}
}
