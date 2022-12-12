package optiTrack.examples;

import optiTrack.MocapDataClient;
import optiTrack.MocapRigidBody;
import optiTrack.MocapRigidbodiesListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Example_TrackSingleRigidBody implements MocapRigidbodiesListener
{
   private int rigidBodyIdToTrack = 1;
   private MocapDataClient mocapClient;
   private DecimalFormat dc = new DecimalFormat("#.00");

   public Example_TrackSingleRigidBody(int interfaceId)
   {
      mocapClient = new MocapDataClient(interfaceId);
      mocapClient.registerRigidBodiesListener(this);
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

      new Example_TrackSingleRigidBody(interfaceId);
   }

   @Override
   public void updateRigidbodies(ArrayList<MocapRigidBody> listOfRigidbodies)
   {
      for (MocapRigidBody rb : listOfRigidbodies)
      {
         if (rb.getId() == rigidBodyIdToTrack)
         {
            System.out.println(rb.toString());
            System.out.println("Receiving at: " + dc.format(mocapClient.getMocapDataReceivingFrequency()) + " Hz");
         }
      }
   }
}
