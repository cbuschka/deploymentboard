class DashboardStore {

    constructor() {
        this.state = {
            environments: {}
        };
    }

    onDashboardStateLoaded({data}) {
        const {state} = data;
        this.state = state;
    }

    appendDataTo(target) {
        target.dashboard = {state: this.state};
    }
}

export const dashboardStore = new DashboardStore();
