import React from 'react';
import {dispatcher} from "@cbuschka/flux";
import {issueStreamsStore} from "./IssueStreamsStore";
import {loadIssueStreams} from "./LoadIssueStreamsAction";
import {IssuesMatrix} from "./IssuesMatrix";
import './IssueStreamsPage.css';

export class IssueStreamsPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {systems: []}
    }

    componentDidMount() {
        dispatcher.addHandler(issueStreamsStore);
        dispatcher.subscribe(this._onChange);
        this._reloadIssues();
    }

    componentWillUnmount() {
        window.clearTimeout(this.reloadTimer);
        dispatcher.unsubscribe(this._onChange);
        dispatcher.removeHandler(issueStreamsStore);
    }

    _reloadIssues = () => {
        loadIssueStreams()
            .finally(() => {
                this.reloadTimer = window.setTimeout(this._reloadIssues, 5000);
            });
    }

    _onChange = ({data}) => {
        const {issueStreams: {issueStreams}} = data;
        this.setState({issueStreams});
    };


    render() {
        const {issueStreams} = this.state;

        return <div className="IssuesPage">
            <IssuesMatrix issueStreams={issueStreams}/>
        </div>;
    }
}
