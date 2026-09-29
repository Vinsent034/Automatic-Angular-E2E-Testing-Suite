export interface CastMember {
  name: string;
  role: string;
}

export interface Review {
  id: number;
  author: string;
  rating: number;
  comment: string;
  date: string;
}

export interface Movie {
  id: number;
  title: string;
  year: number;
  genre: string;
  rating: number;
  director: string;
  synopsis: string;
  runtime: number;
  cast: CastMember[];
  favorite: boolean;
  reviews: Review[];
}
