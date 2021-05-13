class IssueStreamsStore {

  constructor() {
    this.state = {
      ok: true,
      message: '',
      issueStreams: [],
      issues: {}
    };
  }

  onIssueStreamsLoaded({data}) {
    const {issueStreams, issues} = data;
    this.state = {issueStreams, issues};
  }

  onLoadingIssueStreamsFailed({data}) {
    const {message} = data;
    this.state = {ok: false, message};
  }

  appendDataTo(target) {
    target.issueStreams = {...this.state};
  }
}

export const issueStreamsStore = new IssueStreamsStore();
