import {
    Component, OnInit, OnDestroy, Input, OnChanges, SimpleChanges,
    SimpleChange
} from '@angular/core';
import { Response } from '@angular/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { EventManager, ParseLinks, PaginationUtil, JhiLanguageService, AlertService } from 'ng-jhipster';

import { Source } from './source.model';
import { SourceService } from './source.service';
import { ITEMS_PER_PAGE, Principal } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';
import {Project} from "../project/project.model";

@Component({
    selector: 'jhi-source',
    templateUrl: './source.component.html'
})
export class SourceComponent implements OnInit, OnDestroy , OnChanges {

    @Input() project: Project;
    private _project: Project;
    sources: Source[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private sourceService: SourceService,
        private alertService: AlertService,
        private eventManager: EventManager,
        private principal: Principal
    ) {
        this.jhiLanguageService.setLocations(['source' , 'project' , 'projectStatus']);
    }

    ngOnInit() {
        if(this.project) {
            this.loadAllFromProject();
        }
        else {
            this.loadAll();
        }
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInDevices();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: Source) {
        return item.id;
    }
    registerChangeInDevices() {
        this.eventSubscriber = this.eventManager.subscribe('sourceListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
    loadAll() {
        this.sourceService.query().subscribe(
            (res: Response) => {
                this.sources = res.json();
            },
            (res: Response) => this.onError(res.json())
        );
    }
    private loadAllFromProject() {
        this.sourceService.findAllByProject({
            projectId: this.project.id}).subscribe(
            (res: Response) => {
                this.sources = res.json();
            },
            (res: Response) => this.onError(res.json())
        );
    }

    ngOnChanges(changes: SimpleChanges) {
        const project: SimpleChange = changes.project;
        this.project = project.currentValue;
        this.loadAllFromProject();
    }
}
