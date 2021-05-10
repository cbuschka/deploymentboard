import React from 'react';
import {dispatcher} from "@cbuschka/flux";
import {issuesStore} from "./IssuesStore";
import {loadIssues} from "./LoadIssuesAction";
import {Matrix} from "./Matrix";
import './IssuesPage.css';

export class IssuesPage extends React.Component {

    constructor(props) {
        super(props);

        this.state = {systems: []}
    }

    componentDidMount() {
        dispatcher.addHandler(issuesStore);
        dispatcher.subscribe(this._onChange);
        this._reloadIssues();
    }

    componentWillUnmount() {
        window.clearTimeout(this.reloadTimer);
        dispatcher.unsubscribe(this._onChange);
        dispatcher.removeHandler(issuesStore);
    }

    _reloadIssues = () => {
        loadIssues()
            .finally(() => {
                this.reloadTimer = window.setTimeout(this._reloadIssues, 5000);
            });
    }

    _onChange = ({data}) => {
        const {issues: {systems}} = data;
        this.setState({systems});
    };


    render() {
        const {systems} = this.state;

        return <div className="IssuesPage">
            <Matrix systems={systems}/>
        </div>;
    }
}
