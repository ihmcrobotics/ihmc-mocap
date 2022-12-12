package optiTrack.examples;

import optiTrack.MocapDataClient;
import optiTrack.MocapRigidBody;
import optiTrack.MocapRigidbodiesListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Example_TrackMultipleRigidBodies implements MocapRigidbodiesListener
{
   private MocapDataClient mocapClient;
   private DecimalFormat dc = new DecimalFormat("#.00");

   public Example_TrackMultipleRigidBodies(int interfaceId)
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

      new Example_TrackMultipleRigidBodies(interfaceId);
   }

   @Override
   public void updateRigidbodies(ArrayList<MocapRigidBody> listOfRigidbodies)
   {
      System.out.println("\n\n>> START DATA RECEIVED: ");
      System.out.println("# of RigidBodies: " + listOfRigidbodies.size());

      for (MocapRigidBody rigidBody : listOfRigidbodies)
      {
         System.out.println(rigidBody.toString());
      }

      System.out.println("Receiving at: " + dc.format(mocapClient.getMocapDataReceivingFrequency()) + " Hz");
      System.out.println("<< END DATA RECEIVED ");
   }
}
