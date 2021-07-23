package com.mtdaps.obdfuel.util;

import android.util.Log;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OBDValues {

    private static FindFuelTypeCommand findFuelTypeCommand = new FindFuelTypeCommand();
    private static boolean error = false;

    public static Thread check(InputStream socketInputStream, OutputStream socketOutputStream) {
        OBDValues.error = false;

        // Check if the device is an OBD by calling a OBD method.
        Thread thread = new Thread(() -> {
            try {
                findFuelTypeCommand.run(socketInputStream,socketOutputStream);
                //Log.println(Log.ERROR,"","Check1");
            } catch (IOException e) {
                e.printStackTrace();
                OBDValues.error = true;
            } catch (InterruptedException e) {
                OBDValues.error = true;
                e.printStackTrace();
            } catch (Exception e){
                OBDValues.error = true;
                e.printStackTrace();
            }
        });

        return thread;
    }

    public static boolean getError(){
        return error;
    }

    public static void setError(boolean error){
        OBDValues.error = error;
    }

}
