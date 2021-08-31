defmodule Lily do
  use Application

  def start(_type, _args) do
    children = [Lily.Consumer]
    Supervisor.start_link(children, strategy: :one_for_one)
  end
end
