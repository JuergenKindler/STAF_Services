package org.jki.staf.service.util
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo

before_each "Create new ActionExtractor and RequestInfo object",{
	def extractor = new ActionExtractor()
	def requestInfo = new RequestInfo("", "", "", "", 0, 0, false, 0, "", 0, "", "", "")
}

scenario "Extracting action from empty request should fail",{
	given "the request string is empty",{
		requestInfo.request = ""
	}
	
	when "trying to extract an action", {
		action = extractor.getAction(requestInfo)
	}
	
	then "the action should be empty", {
		action.shouldBe ""
	}
}