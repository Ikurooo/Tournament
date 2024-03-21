import { Component, OnInit } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, of } from 'rxjs';


@Component({
  selector: 'app-tournament-edit',
  templateUrl: './tournament-edit.component.html',
  standalone: true,
  styleUrls: ['./tournament-edit.component.scss']
})
export class TournamentEditComponent implements OnInit {
  ngOnInit(): void {
  }

}
