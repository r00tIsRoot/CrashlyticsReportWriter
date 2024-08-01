import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class HTMLConverter {

    data class IssueLink(val href: String, val text: String)

    fun extractIssueLinks(html: String): List<IssueLink> {
        // Parse the HTML document
        val doc: Document = Jsoup.parse(html)

        // Find all <a> tags that contain an issue-caption-table-cell
        val issueLinks = doc.select("a:has(issue-caption-table-cell)")
        val title = doc.select("div[data-test-id=titleWrapper] > span.copy-target")
        val subTitle = doc.select("div[data-test-id=subtitleWrapper] > span.copy-target")
        val eventCount = doc.select("div.sparkline-container")
        val userCount = doc.select(
            "td.mat-mdc-cell.mdc-data-table__cell.cdk-cell.users.hide-at-mobile.cdk-column-userCount.mat-column-userCount.ng-star-inserted > a")

        // Create a list to hold the extracted issue links
        val issueLinkList = mutableListOf<IssueLink>()

        // Iterate over each <a> element and extract the necessary information
        issueLinks.forEachIndexed { index, element ->
            // Get the href attribute
            val href = element.attr("href")
            val issueId = extractIssueId(href)

            // Get the text inside the <a> tag
            val text = issueId +
                    " / \n" + title[index].text() +
                    " / \n" + subTitle[index].text() +
                    " / \n" + eventCount[index].text() +
                    " / \n" + userCount[index].text()

            // Create an IssueLink object and add it to the list
            issueLinkList.add(IssueLink(href, text))
        }

        // Return the list of IssueLink objects
        return issueLinkList
    }

    fun extractIssueId(input: String): String? {
        // 정규 표현식 패턴 정의
        val regex = """/issues/([^?]+)""".toRegex()

        // 정규 표현식에 매치되는 부분 찾기
        val matchResult = regex.find(input)

        // 매치 결과가 있으면 그룹을 반환
        return matchResult?.groups?.get(1)?.value
    }
}