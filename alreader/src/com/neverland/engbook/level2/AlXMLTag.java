package com.neverland.engbook.level2;

import java.util.ArrayList;

public class AlXMLTag {
	public int					tag;
	public boolean					closed;
	public boolean					ended;
	public int					start_pos;
	
	public int					aname;
	public StringBuilder						aval = new StringBuilder();

	public ArrayList<AlXMLTagParam>		attr = new ArrayList<AlXMLTagParam>(16);

	public final void resetTag(int start_position) {
		clearTag();
		closed = false;
		ended = false;
		start_pos = start_position;	
		resetAttr();
	}
	
	public final void add2Tag(char val){
		tag = (tag * 31) + Character.toLowerCase(val);
	}
	
	public final void clearTag(){
		tag = 0x00;
	};

	public final String getATTRValue(int param){
		for (int i = 0; i < attr.size(); i++) {
			if (attr.get(i).name == param) {// && attr.get(i).value.length() > 0) {
				return attr.get(i).value;//.toString();
			}
		}
		return null;
	}

	/*public final boolean getATTRValue(int param, String out){
		for (int i = 0; i < attr.size(); i++) {
			if (attr.get(i).name == param && attr.get(i).value.length() > 0) {				
				out = attr.get(i).value.toString();				
				return true;
			}
		}
		return false;
	}*/

	public final void resetAttr(){
		attr.clear();
		aval.setLength(0);
		aname = 0x00;
	};
	
	public final void add2AttrName(char val){
		aname = (aname * 31) + Character.toLowerCase(val);
	};

	public final void add2AttrValue(char val){
		if (aval.length() < AlAXML.LEVEL2_XML_PARAMETER_VALUE_LEN)
			aval.append(val);
	};
	
	public final void add2AttrValue(String val){
		aval.append(val);
	};
	
	public final void add2AttrValue(StringBuilder val){
		aval.append(val);
	};
	
	public final void clearAttrName(){
		aname = 0x00;
		aval.setLength(0);
	};
	
	public final void clearAttrVal(){	
		aval.setLength(0);
	};

	public final void addAttribute(){
		AlXMLTagParam a = new AlXMLTagParam();
		a.name = aname;
		if (aval.length() > 0)
			a.value = aval.toString();
		attr.add(a);
	};
}
