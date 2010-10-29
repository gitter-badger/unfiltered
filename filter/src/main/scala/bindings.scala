package unfiltered.filter

import unfiltered.JEnumerationIterator
import unfiltered.response.HttpResponse
import unfiltered.request.HttpRequest
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import unfiltered.Cookie
import unfiltered.util.NonNull

private [filter] class RequestBinding(req: HttpServletRequest) extends HttpRequest(req) {
  def inputStream = req.getInputStream
  def reader = req.getReader
  def protocol = req.getProtocol
  def method = req.getMethod
  def requestURI = req.getRequestURI
  def contextPath = req.getContextPath
  def parameterNames = new JEnumerationIterator(
    req.getParameterNames.asInstanceOf[java.util.Enumeration[String]]
  )
  def parameterValues(param: String) = req.getParameterValues(param)
  def headers(name: String) = new JEnumerationIterator(
    req.getHeaders(name).asInstanceOf[java.util.Enumeration[String]]
  )
  lazy val cookies =
    (List[Cookie]() /: req.getCookies)((l, c) => 
      Cookie(c.getName, c.getValue, NonNull(c.getDomain), NonNull(c.getPath), NonNull(c.getMaxAge), NonNull(c.getSecure)) :: l)
}

private [filter] class ResponseBinding(res: HttpServletResponse) extends HttpResponse(res) {
  def setContentType(contentType: String) = res.setContentType(contentType)
  def setStatus(statusCode: Int) = res.setStatus(statusCode)
  def getWriter() = res.getWriter
  def getOutputStream() = res.getOutputStream
  def sendRedirect(url: String) = res.sendRedirect(url)
  def addHeader(name: String, value: String) = res.addHeader(name, value)
  def cookies(resCookies: Seq[Cookie]) = {
    import javax.servlet.http.{Cookie => JCookie}
    resCookies.foreach { c =>
      val jc = new JCookie(c.name, c.value)
      if(c.domain.isDefined) jc.setDomain(c.domain.get)
      if(c.path.isDefined) jc.setPath(c.path.get)
      if(c.maxAge.isDefined) jc.setMaxAge(c.maxAge.get)
      if(c.secure.isDefined) jc.setSecure(c.secure.get)
      res.addCookie(jc)
    }
  }
}
