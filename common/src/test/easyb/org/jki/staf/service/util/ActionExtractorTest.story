package org.jki.staf.service.util
import org.jki.staf.service.util.ActionExtractor
import com.ibm.staf.service.STAFServiceInterfaceLevel30.RequestInfo

before_each "Create new ActionExtractor and RequestInfo object",{
	requestInfo = new RequestInfo("", "", "", "", 0, 0, false, 0, "", 0, "", "", "")
	extractor = new ActionExtractor()
}

scenario "Extracting action from empty request should be empty",{
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


scenario "Extracting action from a request with blank should be empty",{
	given "the request string is blank",{
		requestInfo.request = " "
	}
	
	when "trying to extract an action", {
		action = extractor.getAction(requestInfo)
	}
	
	then "the action should be empty", {
		action.shouldBe ""
	}
}



scenario "Extracting action from a request with a leading and trailing blanks should work",{
	given "the request string contains a leading and trailing blanks",{
		requestInfo.request = "  Hasi  "
	}
	
	when "trying to extract an action", {
		action = extractor.getAction(requestInfo)
	}
	
	then "the action should be empty", {
		action.shouldBe "HASI"
	}
}