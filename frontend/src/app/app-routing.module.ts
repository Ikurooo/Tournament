// app-routing.module.ts

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HorseCreateEditComponent, HorseCreateEditMode } from './component/horse/horse-create-edit/horse-create-edit.component';
import { HorseComponent } from './component/horse/horse.component';
import { HorseInfoComponent } from "./component/horse/horse-info/horse-info.component";
import { TournamentCreateComponent } from "./component/tournament/tournament-create/tournament-create.component";
import { TournamentStandingsComponent } from "./component/tournament/tournament-standings/tournament-standings.component";
import {HorseDeletedComponent} from "./component/horse/horse-deleted/horse-deleted.component";
import {TournamentComponent} from "./component/tournament/tournament.component";
import {HorseDeletionFailedComponent} from "./component/horse/horse-deletion-failed/horse-deletion-failed.component";

const routes: Routes = [
  { path: '', redirectTo: 'horses', pathMatch: 'full' },
  {
    path: 'horses', children: [
      { path: '', component: HorseComponent },
      { path: 'create', component: HorseCreateEditComponent, data: { mode: HorseCreateEditMode.create } },
      { path: 'edit/:id', component: HorseCreateEditComponent, data: { mode: HorseCreateEditMode.edit } },
      { path: 'info/:id', component: HorseInfoComponent },
      { path: 'deletion-successful', component: HorseDeletedComponent },
      { path: 'deletion-failed', component: HorseDeletionFailedComponent },
    ]
  },
  {
    path: 'tournaments', children: [
      { path: '', component: TournamentComponent },
      { path: 'create', component: TournamentCreateComponent },
      { path: 'standings/:id', component: TournamentStandingsComponent }
    ]
  },
  { path: '**', redirectTo: 'horses' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
