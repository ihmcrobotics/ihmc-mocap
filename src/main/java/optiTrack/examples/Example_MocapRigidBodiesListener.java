package optiTrack.examples;

import optiTrack.MocapDataClient;
import optiTrack.MocapRigidBody;
import optiTrack.MocapRigidbodiesListener;

import java.util.ArrayList;

public class Example_MocapRigidBodiesListener implements MocapRigidbodiesListener
{
   int rigidBodyIdToTrack = 1;

   public Example_MocapRigidBodiesListener(int interfaceId)
   {
      MocapDataClient mocapClient = new MocapDataClient(interfaceId);
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

      Example_MocapRigidBodiesListener mocapClientExample = new Example_MocapRigidBodiesListener(interfaceId);
   }

   public int getTrackingId()
   {
      return rigidBodyIdToTrack;
   }

   @Override
   public void updateRigidbodies(ArrayList<MocapRigidBody> listOfRigidbodies)
   {
      for (MocapRigidBody rb : listOfRigidbodies)
      {
         System.out.println(rb.toString());
      }
   }
}
