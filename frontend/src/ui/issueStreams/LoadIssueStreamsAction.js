import {httpClient} from "../HttpClient";
import {dispatcher} from '@cbuschka/flux';

export const loadIssueStreams = async () => {

  return httpClient.doGet("/issueStreams/state")
    .then(res => res.json())
    .then(res => {
      const {issueStreams} = res;
      return dispatcher.dispatch({"type": "issueStreamsLoaded", data: {issueStreams}});
    })
    .catch((e) => {
      return dispatcher.dispatch({"type": "loadingIssueStreamsFailed", data: {message: e.message}});
    })
};
