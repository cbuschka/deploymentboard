import {httpClient} from "../HttpClient";
import {dispatcher} from '@cbuschka/flux';

export const loadIssues = async () => {

  return httpClient.doGet("/issues/state")
    .then(res => res.json())
    .then(res => {
      const {issueStreams} = res;
      return dispatcher.dispatch({"type": "issuesLoaded", data: {issueStreams}});
    })
    .catch((e) => {
      return dispatcher.dispatch({"type": "loadingIssuesFailed", data: {message: e.message}});
    })
};
