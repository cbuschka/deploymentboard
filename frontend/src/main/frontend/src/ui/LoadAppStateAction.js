import {httpClient} from "./HttpClient";
import {dispatcher} from '@cbuschka/flux';

export const loadAppState = async () => {

  return httpClient.doGet("/version")
    .then(res => res.json())
    .then(res => {
      const {version} = res;
      return dispatcher.dispatch({"type": "appStateLoaded", data: {version: {version}, features: {}}});
    })
    .catch((e) => {
      return dispatcher.dispatch({"type": "loadingAppStateFailed", data: {message: e.message}});
    })
};
