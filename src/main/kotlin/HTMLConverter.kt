import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class HTMLConverter {

    data class IssueLink(val href: String, val text: String)

    fun extractIssueLinks(html: String): List<IssueLink> {
        // Parse the HTML document
        val doc: Document = Jsoup.parse(html)

        // Find all <a> tags that contain an issue-caption-table-cell
        val issueLinks = doc.select("a:has(issue-caption-table-cell)")

        // Create a list to hold the extracted issue links
        val issueLinkList = mutableListOf<IssueLink>()

        // Iterate over each <a> element and extract the necessary information
        for (element: Element in issueLinks) {
            // Get the href attribute
            val href = element.attr("href")

            // Get the text inside the <a> tag
            val text = element.text()

            // Create an IssueLink object and add it to the list
            issueLinkList.add(IssueLink(href, text))
        }

        // Return the list of IssueLink objects
        return issueLinkList
    }
}