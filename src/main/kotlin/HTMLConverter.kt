import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class HTMLConverter {

    data class IssueLink(
        val issueId: String,
        val url: String,
        val title: String,
        val subTitle: String,
        val minVersion: String?,
        val latestVersion: String,
        val eventCountIn24: Int,
        val userCountIn24: Int,
        val eventCountIn90Days: Int,
        val userCountIn90Days: Int,
    )

    fun extractIssueLinks(html: String, periodRange: CrashPeriodRange): List<IssueLink> {
        // Parse the HTML document
        val doc: Document = Jsoup.parse(html)

        // Find all <a> tags that contain an issue-caption-table-cell
        val issueLinks = doc.select("a:has(issue-caption-table-cell)")
        val titles = doc.select("div[data-test-id=titleWrapper] > span.copy-target")
        val subTitles = doc.select("div[data-test-id=subtitleWrapper] > span.copy-target")
        val versions = doc.select("span.mat-mdc-tooltip-trigger.version-range")
        val eventCounts = doc.select("div.sparkline-container")
        val userCounts = doc.select(
            "td.mat-mdc-cell.mdc-data-table__cell.cdk-cell.users.hide-at-mobile.cdk-column-userCount.mat-column-userCount.ng-star-inserted > a")

        // Create a list to hold the extracted issue links
        val issueLinkList = mutableListOf<IssueLink>()

        // Iterate over each <a> element and extract the necessary information
        issueLinks.forEachIndexed { index, element ->
            // Get the href attribute
            val href = element.attr("href")
            val issueId = extractIssueId(href)

            // Get the text inside the <a> tag
            val versionRange = versions[index].text()

            // Create an IssueLink object and add it to the list
            issueLinkList.add(
                getIssueLink(
                    periodRange = periodRange,
                    issueId = issueId ?: "",
                    url = href ?: "",
                    title = titles[index].text(),
                    subTitle = subTitles[index].text(),
                    minVersion = versionRange.toMinVersion(),
                    latestVersion = versionRange.toLatestVersion(),
                    eventCount = eventCounts[index].text().toIntWithUnit(),
                    userCount = userCounts[index].text().toIntWithUnit(),
                )
            )
        }

        // Return the list of IssueLink objects
        return issueLinkList
    }

    private fun extractIssueId(input: String): String? {
        // 정규 표현식 패턴 정의
        val regex = """/issues/([^?]+)""".toRegex()

        // 정규 표현식에 매치되는 부분 찾기
        val matchResult = regex.find(input)

        // 매치 결과가 있으면 그룹을 반환
        return matchResult?.groups?.get(1)?.value
    }

    private fun getIssueLink(
        periodRange: CrashPeriodRange,
        issueId: String,
        url: String,
        title: String,
        subTitle: String,
        minVersion: String?,
        latestVersion: String,
        eventCount: Int,
        userCount: Int,
    ): IssueLink {
        return when (periodRange) {
            CrashPeriodRange.LastTwentyFourHours -> {
                IssueLink(
                    issueId = issueId,
                    url = url,
                    title = title,
                    subTitle = subTitle,
                    minVersion = minVersion,
                    latestVersion = latestVersion,
                    eventCountIn24 = eventCount,
                    userCountIn24 = userCount,
                    eventCountIn90Days = -1,
                    userCountIn90Days = -1,
                )
            }

            CrashPeriodRange.LastNinetyDays -> {
                IssueLink(
                    issueId = issueId,
                    url = url,
                    title = title,
                    subTitle = subTitle,
                    minVersion = minVersion,
                    latestVersion = latestVersion,
                    eventCountIn24 = -1,
                    userCountIn24 = -1,
                    eventCountIn90Days = eventCount,
                    userCountIn90Days = userCount,
                )
            }
        }
    }
}

enum class CrashPeriodRange {
    LastTwentyFourHours,
    LastNinetyDays,
}

fun String.toMinVersion(): String {
    val index = this.indexOf('–')
    return if (index != -1) {
        this.substring(0, index).trim() // - 앞 부분 반환, 공백 제거
    } else {
        this
    }
}

fun String.toLatestVersion(): String {
    val index = this.indexOf('–')
    return if (index != -1) {
        this.substring(index + 1).trim() // - 뒤 부분 반환, 공백 제거
    } else {
        this // -가 없는 경우 자기 자신 반환
    }
}

fun String.toIntWithUnit(): Int {
    val numberPart = this.substringBefore("천")
        .substringBefore("만")
        .substringBefore("억")
        .toDoubleOrNull() ?: return 0

    return when {
        this.contains("억") -> (numberPart * 100_000_000).toInt() // "억"일 경우 1억을 곱함
        this.contains("만") -> (numberPart * 10_000).toInt() // "만"일 경우 1만을 곱함
        this.contains("천") -> (numberPart * 1_000).toInt() // "천"일 경우 1천을 곱함
        else -> numberPart.toInt() // 단위가 없는 경우 정수로 변환
    }
}
