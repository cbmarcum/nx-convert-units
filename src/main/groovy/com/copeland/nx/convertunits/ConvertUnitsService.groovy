package com.copeland.nx.convertunits

import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.util.HashMap
import java.util.Map

import org.apache.commons.exec.DefaultExecutor
import org.apache.commons.exec.Executor
import org.apache.commons.exec.DefaultExecuteResultHandler
import org.apache.commons.exec.CommandLine
import org.apache.commons.exec.ExecuteWatchdog
import org.apache.commons.exec.ExecuteException
import org.apache.commons.exec.PumpStreamHandler


import java.io.FileNotFoundException
import java.io.UnsupportedEncodingException
import java.util.ArrayList
import java.util.Arrays
// import java.util.logging.Level
// import java.util.logging.Logger
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.apache.commons.io.output.ByteArrayOutputStream

import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager

import java.util.concurrent.TimeUnit

class ConvertUnitsService {

    private static final Logger logger = LogManager.getLogger(ConvertUnitsService.class) // associated with this class by default

    ConvertUnitsModel model

    void runUgConvert(ConvertUnitsModel model) throws Exception {

        this.model = model

        final long jobTimeout = 60 * 60 * 1000 // 1 hour - was 120000 2 minutes
        final boolean runInBackground = false
        final File convertFile = new File(model.inputFile)
        final File outputDir = new File(model.outputDir)
        final boolean isMM = model.mmUnits

        UgpcResultHandler ugpcResult

        ByteArrayOutputStream osOut = new ByteArrayOutputStream()
        ByteArrayOutputStream osErr = new ByteArrayOutputStream()

        try {
            // converting takes around 60 seconds
            // println("[main] Preparing convert job ...")
            // log.info("TEST OUTPUT")

            // logger.info("[main] Preparing convert job ...")
            // model.setStatus("[main] Preparing convert job ...")
            // use our method instead
            setStatus("[main] Preparing convert job ...")
            ugpcResult = convert(convertFile, outputDir, isMM, jobTimeout, runInBackground, osOut, osErr)
            // println("[main] Successfully sent the convert job ...")
            // logger.info("[main] Successfully sent the convert job ...")
            // model.setStatus("[main] Successfully sent the convert job ...")
            setStatus("[main] Successfully sent the convert job ...")
        } catch (final Exception e) {
            // System.err.println("[main] Execution of the convert job failed : " + convertFile.absolutePath)
            logger.error("[main] Execution of the convert job failed : " + convertFile.absolutePath)
            model.setStatus("[main] Execution of the convert job failed ! See Log")
            e.printStackTrace()
            throw e
        }

        // come back to check the print result
        // println("[main] waiting for the convert job to finish...")
        // logger.info("[main] waiting for the convert job to finish...")
        // model.setStatus("[main] waiting for the convert job to finish...")
        setStatus("[main] waiting for the convert job to finish...")
        ugpcResult.waitFor()
        // println("[main] convert job is complete... Exiting...")
        // model.setStatus("[main] convert job is complete... Exiting...")
        // logger.info("[main] convert job is complete... Exiting...")
        setStatus("[main] convert job is complete... Exiting...")
        // DEBUG
        // println("osOut...")
        // println(osOut.toString())
        // logger.info("osOut...")
        // logger.info(osOut.toString())

        // println("osErr...")
        // println(osErr.toString())
        // logger.error("osErr...")
        // logger.error(osErr.toString())


        logger.info("add output to model...")
        // add output stream to model
        model.masterList.addAll(new ArrayList<String>(
                Arrays.asList(osOut.toString().split(System.getProperty("line.separator"))))
        )

        logger.info("model.masterList size:{}", model.masterList.size())

        // String lastStr = model.masterList.getAt(-1)
        // println "last string = $lastStr"
        // model.masterList.putAt(0, lastStr)
        // model.masterList.remove(-1)
        logger.info("creating tempList...")
        ArrayList<String> tempList = new ArrayList()
        // tempList.add(model.masterList.pop())
        // tempList.add(model.masterList.pop())
        // tempList.add(model.masterList.pop())
        // tempList.add(model.masterList.pop())

        // model.masterList.putAt(3, tempList[3])
        // model.masterList.putAt(2, tempList[2])
        // model.masterList.putAt(1, tempList[1])
        // model.masterList.putAt(0, tempList[0])
        logger.info("going into loop...")

        while (model.masterList[-1] != "") {
            // logger.info("in the loop...")
            // add a blank line which will end up after status lines moved to top
            // println "list starting with $model.masterList.size() items"
            // model.masterList.add(0, System.getProperty("line.separator"))
            // println "list after blank line has $model.masterList.size() items"
            // String tempStr = model.masterList.pop()
            String tempStr = model.masterList.removeLast()
            // println "list has $model.masterList.size() items after pop"
            // println "adding $tempStr"
            model.masterList.add(0, tempStr)
            // println "list has now has $model.masterList.size() items after putAt"
            // println()
        }
        logger.info("exited the loop...")

        // logger.info("Exiting")
        // setStatus("Exiting")
        // System.exit(0)

    }


    UgpcResultHandler convert(final File inFile, final File outFile,
                                     final boolean isMM, final long jobTimeout,
                                     final boolean runInBackground,
                                     ByteArrayOutputStream osOut, ByteArrayOutputStream osErr)
            throws IOException {

        int exitValue
        ExecuteWatchdog watchdog = null
        UgpcResultHandler resultHandler

        // build up the command line to using a 'java.io.File'


        // BEGIN PRODUCTION
        final CommandLine commandLine = new CommandLine("ug_convert_part")

        // if mmUnits are true
        if (isMM) {
            // println "converting to millimeters"
            // model.status = "Converting to millimeters..."
            setStatus("Converting to millimeters...")
            commandLine.addArgument("-mm")
        } else {
            // println "converting to inches"
            // model.status = "Converting to inches..."
            setStatus("Converting to inches...")
            commandLine.addArgument("-in")
        }
        // option for output directory
        commandLine.addArgument("-o")
        commandLine.addArgument('${outFile}') // changed to single quotes for groovy

        // if inFile is a directory add -d
        if (inFile.directory) {
            // println "converting a directory"
            // model.status = "Converting a directory..."
            setStatus("Converting a directory...")
            commandLine.addArgument("-d")
        } else {
            // println "converting a file"
            setStatus("Converting a file")
        }

        // put in a variable for target file
        commandLine.addArgument('${inFile}') // changed to single quotes for groovy


        // this file is the target file to operate on
        final Map<String, File> map = new HashMap<String, File>()
        map.put("inFile", inFile)
        map.put("outFile", outFile)

        // replace the variable
        commandLine.setSubstitutionMap(map)
        // end PRODUCTION


        /*
        // BEGIN DEV
        final CommandLine commandLine = new CommandLine("ls")

        // so we have a test option
        if (inFile.directory) {
        println "converting a directory"
        model.status = "Converting a directory..."
        commandLine.addArgument("-al")
        }

        // put in a variable for target file
        commandLine.addArgument('${inFile}') // changed to single quotes for groovy

        // this file is the target file to operate on
        final Map<String, File> map = new HashMap<String, File>()
        map.put("inFile", inFile)

        // replace the variable
        commandLine.setSubstitutionMap(map)
        // end DEV
         */

        // create the executor and consider the exitValue '0' as success
        final Executor executor = new DefaultExecutor()
        // executor.setExitValue(0); // should be 0

        // create a watchdog if requested
        if (jobTimeout > 0) {
            watchdog = new ExecuteWatchdog(jobTimeout)
            executor.setWatchdog(watchdog)
        }

        PumpStreamHandler streamHandler = new PumpStreamHandler(osOut, osErr)
        executor.setStreamHandler(streamHandler)

        // pass a "ExecuteResultHandler" when doing background process
        if (runInBackground) {
            // println("[convert] Executing non-blocking convert job  ...")
            // model.setStatus("[convert] Executing non-blocking convert job  ...")
            setStatus("[convert] Executing non-blocking convert job  ...")
            resultHandler = new UgpcResultHandler(watchdog)
            executor.execute(commandLine, resultHandler)
        } else {
            // println("[convert] Executing blocking convert job  ...")
            // model.setStatus("[convert] Executing blocking convert job  ...")
            setStatus("[convert] Executing blocking convert job  ...")
            exitValue = executor.execute(commandLine)
            resultHandler = new UgpcResultHandler(exitValue)
        }

        return resultHandler
    }

    // use to set model status and pause 0.5 sec for user to read status line in UI
    // also output same message to system.out and logger
    private void setStatus(String s) {
        System.out.println(s);
        logger.info(s)
        model.setStatus(s);
        try {
            // TimeUnit.MILLISECONDS.sleep(500);
            sleep(500)
        } catch (InterruptedException ex) {
            Logger.getLogger(ConvertUnitsService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }




} // AppService

class UgpcResultHandler extends DefaultExecuteResultHandler {

    private ExecuteWatchdog watchdog

    UgpcResultHandler(final ExecuteWatchdog watchdog) {
        this.watchdog = watchdog
    }

    UgpcResultHandler(final int exitValue) {
        super.onProcessComplete(exitValue)
    }

    @Override
    void onProcessComplete(final int exitValue) {
        super.onProcessComplete(exitValue)
        // println("[resultHandler] The job was successfully converted ...")
        // model.setStatus("[resultHandler] The job was successfully converted ...")
        // setStatus("[resultHandler] The job was successfully converted ...")
    }

    @Override
    void onProcessFailed(final ExecuteException e) {
        super.onProcessFailed(e);
        if (watchdog != null && watchdog.killedProcess()) {
            System.err.println("[resultHandler] The convert process timed out")
            model.setStatus("[resultHandler] The convert process timed out")
            logger.error("[resultHandler] The convert process timed out" + e.getMessage())

        } else {
            System.err.println("[resultHandler] The convert process failed to do : " + e.getMessage())
            model.setStatus("[resultHandler] The convert process failed ! ")
            logger.error("[resultHandler] The convert process failed to do : " + e.getMessage())

        }
    }
} // ResultHandler