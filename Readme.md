# 사용법
1. "json 형식 입력"을 선택하고, 입력란에 전날 json 문자열을 입력 후 "데이터 처리" 버튼을 클릭
2. 24시간 기준 HTML 문자열 복사
3. "24시간 기준 HTML 입력"을 선택하고, 입력란에 붙여넣기
4. 90일 기준 HTML 문자열 복사하여 입력


## html 문자열 복사 방법
1. Crashlytics 메인 화면에서 기준 시간 설정하여 목록 로드
2. 각 이슈들의 타이틀부분에 마우스를 한번씩 오버
   - 링크 url이 javascript code에 의해 갱신되기 때문
   - issueId도 해당 링크 url을 기반으로 얻음
3. F12키를 눌 개발자도구 활성화
4. 최상단 혹은 최하단 html태그를 우클릭하여 "html로 수정"
5. 전체 html 문자열 복사
6. 이슈 목록의 페이지가 더 있을 경우 페이지 이동 후 2번 부터 반복


## 마우스 오버 자동화
1. 개발자 도구
2. 콘솔
3. 아래 코드 기입 후 엔터
```javaScript
// 모든 a 태그를 선택
const links = document.querySelectorAll("a");

// 각 링크에 대해 href 값을 업데이트
links.forEach(link => {
    // 마우스 오버 이벤트가 발생했을 때의 실제 href 값을 가져옵니다.
    link.dispatchEvent(new MouseEvent('focus'));
    
    // 실제 href 값을 가져와서 업데이트
    const realHref = link.getAttribute("href");
    if (realHref !== "#" && realHref) {
        link.setAttribute("data-real-href", realHref); // 기존 href 값을 data 속성에 저장
    }
});

// 이제 모든 a 태그의 href를 data-real-href 속성으로 업데이트
links.forEach(link => {
    const realHref = link.getAttribute("data-real-href");
    if (realHref) {
        link.setAttribute("href", realHref); // href를 실제 값으로 업데이트
    }
});

// 확인을 위해 모든 a 태그의 href 값 출력
links.forEach(link => {
    console.log(link.textContent, link.getAttribute("href"));
});
```
4. 페이지 별로 반복
