class DashboardStore {

    constructor() {
        this.state = {
            environments: {}
        };
    }

    onDashboardStateLoaded({data}) {
        const {state} = data;
        this.state = {ok: true, ...state};
    }

    onLoadingDashboardStateFailed({data}) {
        const {message} = data;
        this.state = {ok: false, message};
    }

    appendDataTo(target) {
        target.dashboard = {state: this.state};
    }
}

export const dashboardStore = new DashboardStore();
