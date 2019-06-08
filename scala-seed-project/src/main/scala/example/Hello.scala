package example

import net.ruippeixotog.scalascraper.browser.JsoupBrowser

object Hello extends Greeting with App {
  println(greeting)

  val browser = JsoupBrowser()
  val doc = browser.parseFile("core/src/test/resources/example.html")
  val doc2 = browser.get("http://example.com")
}

trait Greeting {
  lazy val greeting: String = "hello"
}
