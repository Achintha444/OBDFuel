package com.mtdaps.obdfuel.util;

import android.util.Log;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.control.ModuleVoltageCommand;
import com.github.pires.obd.commands.engine.MassAirFlowCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.AirFuelRatioCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.fuel.FindFuelTypeCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;




public class OBDValues {

    private static final FindFuelTypeCommand findFuelTypeCommand = new FindFuelTypeCommand();
    private static final AirFuelRatioCommand airFuelRatioCommand = new AirFuelRatioCommand();
    private static final ConsumptionRateCommand consumptionRateCommand = new ConsumptionRateCommand();
    private static final MassAirFlowCommand massAirFlowCommand = new MassAirFlowCommand();
    private static final ModuleVoltageCommand moduleVoltageCommand = new ModuleVoltageCommand();
    private static final RPMCommand rpmCommand = new RPMCommand();
    private static final SpeedCommand speedCommand = new SpeedCommand();

    private static boolean error = false;

    public static void init() throws IOException, InterruptedException {
        new EchoOffCommand().run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
        new LineFeedOffCommand().run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
        new TimeoutCommand(62).run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
        new SelectProtocolCommand(ObdProtocols.AUTO).run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
    }


    public static Thread check() {

        OBDValues.error = false;

        // Check if the device is an OBD by calling a OBD method.
        Thread thread = new Thread(() -> {
            try {
                init();
                airFuelRatioCommand.run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
                Log.println(Log.ERROR, "airFuelRatio - Check", airFuelRatioCommand.getResult());
            } catch (IOException e) {
                e.printStackTrace();
                OBDValues.error = true;
            } catch (InterruptedException e) {
                OBDValues.error = true;
                e.printStackTrace();
            } catch (Exception e) {
                OBDValues.error = true;
                e.printStackTrace();
            }
        });

        return thread;
    }



//    // UnableToConnectException
//    public static HashMap<String, Object> obdData() {
//        HashMap<String, Object> sendObdDataValues = new HashMap<>();
//        sendObdDataValues.put("FuelType", "a");
//        sendObdDataValues.put("AirFuelRation", "");
//        sendObdDataValues.put("ConusmptionRate", "");
//        sendObdDataValues.put("MassAirFlow", "");
//        sendObdDataValues.put("ModuleVoltageCommand", "");
//        sendObdDataValues.put("SpeedCommand", "");
//        sendObdDataValues.put("RPMCommand", "");
//
//        return sendObdDataValues;
//    }

    // UnableToConnectException
    public static HashMap<String, Object> obdData() {
        HashMap<String, Object> sendObdDataValues = new HashMap<>();

        try {
            //findFuelTypeCommand.run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
            airFuelRatioCommand.run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
            //consumptionRateCommand.run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
            massAirFlowCommand.run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
            moduleVoltageCommand.run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
            speedCommand.run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
            rpmCommand.run(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendObdDataValues.put("AirFuelRation", airFuelRatioCommand.getAirFuelRatio());
        sendObdDataValues.put("MassAirFlow", massAirFlowCommand.getMAF());
        sendObdDataValues.put("ModuleVoltageCommand", moduleVoltageCommand.getVoltage());
        sendObdDataValues.put("SpeedCommand", speedCommand.getMetricSpeed());
        sendObdDataValues.put("RPMCommand", rpmCommand.getRPM());

        return sendObdDataValues;
    }


    public static boolean getError() {
        return error;
    }

    public static void setError(boolean error) {
        OBDValues.error = error;
    }

}
