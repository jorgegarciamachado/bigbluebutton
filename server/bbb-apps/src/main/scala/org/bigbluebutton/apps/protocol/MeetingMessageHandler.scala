package org.bigbluebutton.apps.protocol

import spray.json.JsObject
import spray.json.DeserializationException
import spray.json.DefaultJsonProtocol
import org.bigbluebutton.apps.models.MeetingConfig
import org.bigbluebutton.apps.models.PhoneNumberConfig
import org.bigbluebutton.apps.models.VoiceConfig
import org.bigbluebutton.apps.models.DurationConfig
import org.bigbluebutton.apps.models.UsersConfig
import akka.event.LoggingAdapter
import akka.event.slf4j.SLF4JLogging

object CreateMeetingRequestJsonProtocol1 extends DefaultJsonProtocol {
  implicit val usersDefFormat = jsonFormat2(UsersConfig)
  implicit val durationDefFormat = jsonFormat3(DurationConfig)
  implicit val voiceConfDefFormat = jsonFormat2(VoiceConfig)
  implicit val phoneNumberDefFormat = jsonFormat2(PhoneNumberConfig)
  implicit val meetingDefFormat = jsonFormat11(MeetingConfig)
}

case class CreateMeetingRequestPayload(meeting: MeetingConfig)
case class CreateMeetingRequest(header: Header, payload: MeetingConfig) extends InMessage

trait MeetingMessageHandler extends SLF4JLogging {
  import CreateMeetingRequestJsonProtocol1._
  
  def handleCreateMeetingRequest(header: Header, 
                                 payload: JsObject):Option[InMessage] = {
    val meeting = payload.fields.get("meeting")
    if (meeting != None) {
      try {
        val m = meeting.get.convertTo[MeetingConfig]
        log.debug("Managed to decode create meeting request")
        Some(CreateMeetingRequest(header, m))
      } catch {
        case e: DeserializationException => {
          println(e)
          None
        }
      }
    } else {
      None
    }    
  }
}