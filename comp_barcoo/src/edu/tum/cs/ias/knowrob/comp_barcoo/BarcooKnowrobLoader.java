/*
 * Copyright (c) 2010 Nacer Khalil
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Technische Universiteit Eindhoven nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
*/
package edu.tum.cs.ias.knowrob.comp_barcoo;

import java.util.HashMap;

import ros.*;
import ros.pkg.vision_msgs.msg.aposteriori_position;
import ros.pkg.vision_msgs.msg.cop_answer;
import ros.pkg.vision_msgs.msg.cop_descriptor;
import ros.pkg.vision_srvs.srv.cop_call;
import ros.pkg.vision_srvs.srv.srvjlo;
import ros.pkg.vision_srvs.srv.srvjlo.Response;
import ros.pkg.vision_srvs.srv.srvjlo.Request;
import ros.pkg.vision_msgs.msg.partial_lo;

public class BarcooKnowrobLoader {
	
	static String topic;
	private static HashMap<String, Integer> map;
	private static int objectCount = 0;
	Publisher<ros.pkg.vision_msgs.msg.cop_answer> pub;
	
	public BarcooKnowrobLoader(NodeHandle n, String topic) throws Exception
	{
		this.topic = topic;
		ros.pkg.vision_msgs.msg.cop_answer msgType = new ros.pkg.vision_msgs.msg.cop_answer();
		map = new HashMap<String, Integer>();				
		pub = n.advertise(topic, msgType, 1);				
	}
	
	public String passToKnowrob(String barcode)
	{
		
		//first find appropriate individual id
		Integer id = map.get(barcode);
		if(id == null || id == 0)
		{
			id = 1;
			map.put(barcode, id);
		}
		else
		{
			id++;
			map.remove(barcode);
			map.put(barcode, id);
		}
		
		//create individual by sending cop_answer to rosprolog
		objectCount++;
		
		//	Constructing the cop_answer object
		cop_answer msg = new cop_answer();
		
		msg.perception_primitive = 0;
		msg.error = "";
		aposteriori_position position = new aposteriori_position();
		
		position.objectId = id;
		position.probability = 1;
		position.position = 0;
		
		cop_descriptor descriptor = new cop_descriptor();
		
		descriptor.object_id = 0;
		descriptor.sem_class = "http://www.barcoo.com/barcoo.owl#" + barcode;
		descriptor.type = "BarcooFinder";
		descriptor.quality = 0;
		
		position.models.add(descriptor);
		
		msg.found_poses.add(position);
		pub.publish(msg);
		
		return barcode + "_k"+ id;
	}
	
}
