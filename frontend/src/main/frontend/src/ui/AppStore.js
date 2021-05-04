class AppStore {

    constructor() {
        this.state = {
            features: {},
            version: {}
        };
    }

    onAppStateLoaded({data:{version, features}}) {
      this.state = {version, features};
    }

    appendDataTo(target) {
        target.app = {...this.state};
    }
}

export const appStore = new AppStore();
