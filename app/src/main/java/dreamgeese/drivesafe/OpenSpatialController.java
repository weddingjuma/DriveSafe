package dreamgeese.drivesafe;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.openspatial.ButtonEvent;
import net.openspatial.GestureEvent;
import net.openspatial.OpenSpatialEvent;
import net.openspatial.OpenSpatialException;
import net.openspatial.OpenSpatialService;

public class OpenSpatialController {
    private static final String NAME = "DriveSafe";
    private ServiceConnection mOpenSpatialServiceConnection;
    private OpenSpatialService mOpenSpatialService;
    private VolumeController volumeController;
    private CallController callController;
    private Activity currentActivity;

    public OpenSpatialController(final Activity currActivity,VolumeController volumeCtrl,CallController callCtrl){
        currentActivity=currActivity;
        volumeController=volumeCtrl;
        callController=callCtrl;


        //Creates a background service that listens for events in the nod ring
          mOpenSpatialServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mOpenSpatialService = ((OpenSpatialService.OpenSpatialServiceBinder)service).getService();
                mOpenSpatialService.getConnectedDevices();

                mOpenSpatialService.initialize(NAME, new OpenSpatialService.OpenSpatialServiceCallback() {
                    //when a connection has been made to the nod ring
                    @Override
                    public void deviceConnected(final BluetoothDevice bluetoothDevice) {
                        try {
                            //capture all gesture events
                            mOpenSpatialService.registerForGestureEvents(bluetoothDevice, new OpenSpatialEvent.EventListener() {
                                @Override
                                public void onEventReceived(OpenSpatialEvent event) { //when a new gesture event has been received
                                    GestureEvent gEvent = (GestureEvent) event;
                                    handleGestureEvent(gEvent); //handles the gesture events
                                    Log.d(NAME, gEvent.gestureEventType + "");
                                }
                            });

                            //capture all gesture events
                            mOpenSpatialService.registerForButtonEvents(bluetoothDevice, new OpenSpatialEvent.EventListener() {
                                @Override
                                public void onEventReceived(OpenSpatialEvent event) { //when a new button event has been received
                                    ButtonEvent bEvent = (ButtonEvent) event;
                                    handleTouchEvent(bEvent); //handles the button events
                                    Log.d(NAME, bEvent.buttonEventType + "");
                                }
                            });
                            ViewController.setDisplayConnected(currActivity);

                        } catch (OpenSpatialException e) {
                            Log.e(NAME, "Could Not Register for Gesture Events" + e);
                            ViewController.setDisplayDisconnected(currActivity);;
                        }
                    }

                    @Override
                    public void gestureEventRegistrationResult(BluetoothDevice bluetoothDevice, int i) {
                        Log.d(NAME, "Gesture Event Registration Result: "+i);
                    }

                    @Override
                    public void deviceDisconnected(BluetoothDevice bluetoothDevice) {
                        ViewController.setDisplayDisconnected(currActivity);
                    }

                    @Override
                    public void buttonEventRegistrationResult(BluetoothDevice bluetoothDevice, int i) {
                    }

                    @Override
                    public void pointerEventRegistrationResult(BluetoothDevice bluetoothDevice, int i) {
                    }

                    @Override
                    public void pose6DEventRegistrationResult(BluetoothDevice bluetoothDevice, int i) {
                    }

                    @Override
                    public void motion6DEventRegistrationResult(BluetoothDevice bluetoothDevice, int i) {
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mOpenSpatialService = null;
                ViewController.setDisplayDisconnected(currActivity);
            }
        };
    }//end of constructor

    private void handleGestureEvent(GestureEvent gEvent){
        //outputs the result to the activity so the user can see what gesture he/she is making
        ViewController.showDetectedGestures(currentActivity,gEvent);

        String gesture=gEvent.gestureEventType.toString();
        if(gesture.equals(UserSettings.raiseVolume)){ //for some reason a swipe down is recognized as a swipe app
            volumeController.raiseVolume();
        }
        else if(gesture.equals(UserSettings.lowerVolume)){
            volumeController.lowerVolume();
        }
        else if(gesture.equals(UserSettings.playMusic)){
                volumeController.playMusic();
        }
        else if(gesture.equals(UserSettings.pauseMusic)){
                volumeController.pauseMusic();
        }
        else if(gesture.equals(UserSettings.nextSong)){
            volumeController.nextSong();
        }
        else if(gesture.equals(UserSettings.previousSong)){
            volumeController.previousSong();
        }
        else if(gesture.equals(UserSettings.acceptCall)){
            callController.acceptCall(currentActivity);
        }
        else if(gesture.equals(UserSettings.rejectCall)){
            callController.rejectCall();
        }
        else if(gesture.equals(UserSettings.callNumber)){
            callController.callNumber(currentActivity);
        }
    }
    public void handleTouchEvent(ButtonEvent bEvent){
        String button=bEvent.buttonEventType.toString();
        if(button.equals(UserSettings.raiseVolume)){ //for some reason a swipe down is recognized as a swipe app
            volumeController.raiseVolume();
        }
        else if(button.equals(UserSettings.lowerVolume)){
            volumeController.lowerVolume();
        }
        else if(button.equals(UserSettings.playMusic)){
            volumeController.playMusic();
        }
        else if(button.equals(UserSettings.pauseMusic)){
            volumeController.pauseMusic();
        }
        else if(button.equals(UserSettings.nextSong)){
            volumeController.nextSong();
        }
        else if(button.equals(UserSettings.previousSong)){
            volumeController.previousSong();
        }
        else if(button.equals(UserSettings.acceptCall)){
            callController.acceptCall(currentActivity);
        }
        else if(button.equals(UserSettings.rejectCall)){
            callController.rejectCall();
        }
        else if(button.equals(UserSettings.callNumber)){
            callController.callNumber(currentActivity);
        }
    }

    public ServiceConnection getServiceConnection(){
        return mOpenSpatialServiceConnection;
    }
}
