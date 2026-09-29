import { Component, input } from '@angular/core';
import { CastMember } from '../movie.model';

@Component({
  selector: 'app-cast-row',
  imports: [],
  templateUrl: './cast-row.component.html'
})
export class CastRowComponent {
  readonly member = input.required<CastMember>();
}
