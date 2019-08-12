package com.linty.sonar.params;


import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.codehaus.staxmate.in.SMEvent;
import org.codehaus.staxmate.in.SMFilter;
import org.codehaus.staxmate.in.SMInputCursor;
import static com.linty.sonar.params.ParamTranslator.*;

public class ParamXmlParser {
  
  //Filter is mandatory for SMInputCursor to advance
  private SMFilter filter = new IgnoreSomeRuleElements();

  //Static method called if parameters exist and are not empty
  public static void collectParameters(List<ZamiaParam> params, SMInputCursor ruleParamsCursor)  throws XMLStreamException {
       new ParamXmlParser().parseParameters(params, ruleParamsCursor);
  }
  
  //Collect all parameters into the given List<ZamiaParam> for a rule
  private void parseParameters(List<ZamiaParam> params, SMInputCursor ruleParamsCursor) throws XMLStreamException {  
    SMInputCursor paramCursor = ruleParamsCursor.childCursor(filter).advance();
        while(paramCursor.asEvent() != null) { 
      params.add(collectParameter(paramCursor));
      paramCursor.advance();
    }
  }

  //Parse one parameter section and returns a ZamiaParam filled with it
  private ZamiaParam collectParameter(SMInputCursor paramCursor) throws XMLStreamException {       
    String paramType = paramCursor.getLocalName();
    SMInputCursor contentCursor = paramCursor.childCursor(filter).advance();
        if(STRING_PARAM.equals(paramType)) {
      return collectStringParam(contentCursor);
    } else if (INT_PARAM.equals(paramType)) {
      return collectIntParam(contentCursor);
    } else if (RANGE_PARAM.equals(paramType)) {
      return collectRangeParam(contentCursor);
    } else {
      throw new XMLStreamException("Unknown parameter type : " + paramType);
    }
  }

  //For String parameters
  private ZamiaParam collectStringParam(SMInputCursor contentCursor) throws XMLStreamException {
    ZamiaStringParam sp = new ZamiaStringParam();
    return collectContent(sp, contentCursor)
      .setValue(contentCursor.advance().getElemStringValue());
  }

  //For In parameters
  private ZamiaParam collectIntParam(SMInputCursor contentCursor) throws XMLStreamException {
    ZamiaIntParam ip = new ZamiaIntParam();
    return collectContent(ip, contentCursor)
      .setValue(contentCursor.advance().getElemIntValue());
  }

  //For Range parameters
  private ZamiaParam collectRangeParam(SMInputCursor contentCursor) throws XMLStreamException {
    ZamiaRangeParam rp = new ZamiaRangeParam();
    return collectContent(rp, contentCursor)
      .setMin(contentCursor.advance().getElemIntValue())
      .setMax(contentCursor.advance().getElemIntValue());
  }

  //Fills the paramID and field
  @SuppressWarnings("unchecked")
  private <T extends ZamiaParam> T collectContent(T zp, SMInputCursor contentCursor) throws XMLStreamException {
    return (T) zp
      .setHbParamId(contentCursor.getElemStringValue())
      .setField(contentCursor.advance().getElemStringValue());
  }

  private class IgnoreSomeRuleElements extends SMFilter {   
    @Override
    public boolean accept(SMEvent evt, SMInputCursor caller) throws XMLStreamException {
      if (! evt.hasLocalName()) {
        return false;
      }
      return true;
    }
  }



}
