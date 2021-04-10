import {httpClient} from "../HttpClient";
import {dispatcher} from '@cbuschka/flux';

export const loadDashboardState = async () => {

    return httpClient.doGet("/dashboard/state")
        .then(res => res.json())
        .then(res => {
            return dispatcher.dispatch({"type": "dashboardStateLoaded", data: {state: res}});
        });
};
