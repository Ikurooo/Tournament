import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HorseService } from '../../../service/horse.service';
import { Horse } from "../../../dto/horse";

@Component({
  selector: 'app-edit-horse',
  templateUrl: './edit-horse.component.html',
  standalone: true,
  styleUrls: ['./edit-horse.component.scss']
})
export class EditHorseComponent implements OnInit {
  horseId: string | null = null;
  horse: Horse | null = null; // Initialize a new instance of Horse

  constructor(
    private route: ActivatedRoute,
    private horseService: HorseService,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.horseId = params.get('id');
      this.loadHorseDetails();
    });
  }

  loadHorseDetails(): void {
    if (this.horseId) {
      // Use .subscribe() to get the result of the asynchronous operation
      this.horseService.getById(this.horseId).subscribe(
        (result: Horse) => {
          // Assign the result to the horse property
          this.horse = result;
          console.log("loaded horse?");
        },
        (error) => {
          console.error('Error fetching horse details: { horse ID: ', this.horseId, ' }');
          // Handle error as needed
        }
      );
    }
  }
}
