

Uses REST API to update page in Atlassian Confluence using file created by OABUilder Blueprints generator  

Note: any text in the following, that has "[]" brackets should be replaced (brackets should be removed)

STEP 1: get API token from Confluence, so that you can use REST API
  https://id.atlassian.com/manage-profile/security/api-tokens
    name=blueprints

STEP 2: paste token here.

STEP 3: create page in Confluence and paste URL here.


STEP 4: get the page.
curl -u [jdoe@company.com]:[token here] \
  -X GET \
  -H "Content-Type: application/json" \
  "https://[company].atlassian.net/wiki/rest/api/content/[doc number]?expand=version"

STEP 5: find the version from the  output

STEP 6: run OABuilder Blueprints generator and update using version+1

curl -u [your.email@company.com]:[your_api_token] \
  -X PUT \
  -H "Content-Type: application/json" \
  --data @blueprints.json \   
  "https://[company].atlassian.net/wiki/rest/api/content/[534349500]"



EXAMPLE:


curl -u vvia@oreillyauto.com:ATATT3xFfGF0HO8-YY71QGEiPKMsNDYFJjz2w-pWzCgZI_MQcPwOqocH9T3_m5jYdwtUW4IcO-d9E5lye5dTpJJYzQor9eteMXX8ByF8QEsMkSjqcg1SA9d4GO_we-I8ziRX4UtKw0wM7niNAAZ9XiKnzfbx5Ngi7uFjZamjjJV07LOnE15fLD0=76D5BAD5 \
-X PUT \
-H "Content-Type: application/json" \
--data @blueprints.json \
"https://oreillyauto.atlassian.net/wiki/rest/api/content/536151833"








