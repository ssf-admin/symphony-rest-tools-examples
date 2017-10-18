/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.symphony.tools.example.monitor.checkpod;

import java.io.IOException;

import org.symphonyoss.symphony.tools.rest.SrtDaemonCommand;
import org.symphonyoss.symphony.tools.rest.console.Console;
import org.symphonyoss.symphony.tools.rest.model.osmosis.ComponentStatus;
import org.symphonyoss.symphony.tools.rest.probe.CheckPod;
import org.symphonyoss.symphony.tools.rest.util.command.Flag;

public class HealthcheckDaemon extends SrtDaemonCommand
{
  private static final String PROGRAM_NAME       = "HealthcheckDaemon";

  public static void main(String[] argv) throws IOException
  {
    new HealthcheckDaemon(argv).run();
  }

  private Long delayMillis_ = 10000L;
  
  public HealthcheckDaemon(String[] argv)
  {
    super(PROGRAM_NAME, argv);
  }
  
  @Override
  protected void init()
  {
    super.init();

    withHostName(true);
    withKeystore(false);
    withTruststore(false);
    
    getParser()
    .withFlag(new Flag<Long>("Check Interval in ms", Long.class, (v) -> delayMillis_ = v)
       .withName("delayMillis"));
  }

  @Override
  public void execute()
  {
    Console console = getMultiConsole();
    
    ComponentStatus status = null;
    
    while(true)
    {
      console.reset();
      
      CheckPod  checkPod = new CheckPod(console, getSrtHome());
      
      setSwitch(checkPod, 1, getQuiet());
      setAllFlags(checkPod);
      
      checkPod.prepareToExecute();
      checkPod.execute();
      
      ComponentStatus newStatus = checkPod.getPodObjective().getComponentStatus();
      
      if(newStatus == status)
      {
        println(".");
      }
      else
      {
        status = newStatus;
        
        println("Pod Status changed to " + status);
      }
      
      flush();
      
      try
      {
        Thread.sleep(delayMillis_);
      }
      catch (InterruptedException e)
      {
        error(e, "Interrupted");
        break;
      }
    }
  }

  
}
